package com.businesscard.app.ui.contacts

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.businesscard.app.model.ContactRecord
import com.businesscard.app.us3.auth.PermissionChecker
import com.businesscard.app.us3.auth.PermissionDecision
import com.businesscard.app.us3.auth.PermissionScope
import com.businesscard.app.us3.security.ConsistencyDecision
import com.businesscard.app.us3.security.ConsistencyPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface ContactsDataSource {
    suspend fun getLocal(): ContactRecord?
    suspend fun getRemote(): ContactRecord?
    suspend fun applyLocal(record: ContactRecord)
    suspend fun applyRemote(record: ContactRecord)
}

class ContactsViewModel(
    private val dataSource: ContactsDataSource,
    private val permissionChecker: PermissionChecker,
    private val consistencyPolicy: ConsistencyPolicy
) : ViewModel() {

    private val _state = MutableStateFlow(ContactsUiState())
    val state: StateFlow<ContactsUiState> = _state

    fun onEvent(event: ContactsUiEvent) {
        when (event) {
            ContactsUiEvent.RequestDrivePermission -> handlePermissionRequest()
            is ContactsUiEvent.OnPermissionResult -> handlePermissionResult(event.granted, event.permanentlyDenied)
            ContactsUiEvent.StartConsistencyCheck -> handleConsistencyCheck()
            is ContactsUiEvent.OnConsistencyResolved -> applyDecision(event.decision)
            is ContactsUiEvent.OnConsistencyError -> setError(event.message)
            is ContactsUiEvent.ResolveConflictUseLocal -> applyLocalById(event.contactId)
            is ContactsUiEvent.ResolveConflictUseRemote -> applyRemoteById(event.contactId)
        }
    }

    private fun handlePermissionRequest() {
        val result = permissionChecker.check(setOf(PermissionScope.DRIVE))
        val status = when (result.decision) {
            PermissionDecision.GRANTED -> PermissionStatus.GRANTED
            PermissionDecision.DENIED -> PermissionStatus.DENIED
            PermissionDecision.PERMANENTLY_DENIED -> PermissionStatus.PERMANENTLY_DENIED
        }
        _state.value = _state.value.copy(permissionStatus = status)
    }

    private fun handlePermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        val status = when {
            permanentlyDenied -> PermissionStatus.PERMANENTLY_DENIED
            granted -> PermissionStatus.GRANTED
            else -> PermissionStatus.DENIED
        }
        _state.value = _state.value.copy(permissionStatus = status)
    }

    private fun handleConsistencyCheck() {
        viewModelScope.launch {
            _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.CHECKING, errorMessage = null)
            runCatching {
                val local = dataSource.getLocal()
                val remote = dataSource.getRemote()
                val decision = consistencyPolicy.decide(local?.updatedAt, remote?.updatedAt)
                _state.value = _state.value.copy(
                    consistencyStatus = when (decision) {
                        ConsistencyDecision.USE_LOCAL -> ConsistencyStatus.APPLYING_LOCAL
                        ConsistencyDecision.USE_REMOTE -> ConsistencyStatus.APPLYING_REMOTE
                        ConsistencyDecision.NO_ACTION -> ConsistencyStatus.IDLE
                    },
                    lastDecision = decision,
                    conflictLocal = local,
                    conflictRemote = remote
                )
                when (decision) {
                    ConsistencyDecision.USE_LOCAL -> local?.let { dataSource.applyLocal(it.copy(updatedAt = nowIso())) }
                    ConsistencyDecision.USE_REMOTE -> remote?.let { dataSource.applyRemote(it) }
                    ConsistencyDecision.NO_ACTION -> Unit
                }
                _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.IDLE)
            }.onFailure { ex ->
                setError(ex.message ?: "consistency_error")
            }
        }
    }

    private fun applyDecision(decision: ConsistencyDecision) {
        when (decision) {
            ConsistencyDecision.USE_LOCAL -> _state.value.conflictLocal?.let { applyLocal(it) }
            ConsistencyDecision.USE_REMOTE -> _state.value.conflictRemote?.let { applyRemote(it) }
            ConsistencyDecision.NO_ACTION -> _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.IDLE)
        }
    }

    private fun applyLocal(record: ContactRecord) {
        viewModelScope.launch {
            runCatching {
                _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.APPLYING_LOCAL)
                dataSource.applyLocal(record.copy(updatedAt = nowIso()))
                _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.IDLE)
            }.onFailure { ex ->
                setError(ex.message ?: "apply_local_error")
            }
        }
    }

    private fun applyRemote(record: ContactRecord) {
        viewModelScope.launch {
            runCatching {
                _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.APPLYING_REMOTE)
                dataSource.applyRemote(record)
                _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.IDLE)
            }.onFailure { ex ->
                setError(ex.message ?: "apply_remote_error")
            }
        }
    }

    private fun applyLocalById(contactId: String) {
        val local = _state.value.conflictLocal?.takeIf { it.id == contactId }
        if (local != null) {
            applyLocal(local)
        }
    }

    private fun applyRemoteById(contactId: String) {
        val remote = _state.value.conflictRemote?.takeIf { it.id == contactId }
        if (remote != null) {
            applyRemote(remote)
        }
    }

    private fun setError(message: String) {
        _state.value = _state.value.copy(consistencyStatus = ConsistencyStatus.ERROR, errorMessage = message)
    }

    private fun nowIso(): String =
        OffsetDateTime.now(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
