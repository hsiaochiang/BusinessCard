package com.businesscard.app.ui.contacts

import com.businesscard.app.us3.security.ConsistencyDecision

sealed class ContactsUiEvent {
    data object RequestDrivePermission : ContactsUiEvent()
    data class OnPermissionResult(val granted: Boolean, val permanentlyDenied: Boolean) : ContactsUiEvent()
    data object StartConsistencyCheck : ContactsUiEvent()
    data class OnConsistencyResolved(val decision: ConsistencyDecision) : ContactsUiEvent()
    data class OnConsistencyError(val message: String) : ContactsUiEvent()
    data class ResolveConflictUseLocal(val contactId: String) : ContactsUiEvent()
    data class ResolveConflictUseRemote(val contactId: String) : ContactsUiEvent()
}
