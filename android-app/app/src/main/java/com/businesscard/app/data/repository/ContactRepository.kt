package com.businesscard.app.data.repository

import com.businesscard.app.core.IdGenerator
import com.businesscard.app.core.TimeProvider
import com.businesscard.app.data.db.ContactDao
import com.businesscard.app.data.remote.drive.DriveClient
import com.businesscard.app.data.remote.drive.DriveUploadResult
import com.businesscard.app.data.remote.sheets.SheetsClient
import com.businesscard.app.data.remote.sheets.SheetsReader
import com.businesscard.app.data.remote.sheets.SheetsWriteResult
import com.businesscard.app.model.ContactDraftEntity
import com.businesscard.app.model.ContactQuery
import com.businesscard.app.model.ContactRecord
import com.businesscard.app.model.SortBy
import com.businesscard.app.model.Source
import com.businesscard.app.model.SyncStatus
import com.businesscard.app.telemetry.Telemetry
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

data class ContactDraftForm(
    val name: String,
    val company: String? = null,
    val title: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val tags: List<String> = emptyList(),
    val notes: String? = null,
    val imagePath: String? = null
)

@Suppress("LongParameterList")
class ContactRepository(
    private val dao: ContactDao,
    private val driveClient: DriveClient,
    private val sheetsClient: SheetsClient,
    private val sheetsReader: SheetsReader,
    private val idGenerator: IdGenerator,
    private val timeProvider: TimeProvider,
    private val telemetry: Telemetry
) {

    private val contactsFlow = MutableStateFlow<List<ContactRecord>>(emptyList())

    suspend fun createDraft(form: ContactDraftForm): ContactDraftEntity {
        val id = idGenerator.generate()
        val now = timeProvider.nowIso()
        val draft = ContactDraftEntity(
            localId = UUID.randomUUID().toString(),
            generatedId = id,
            name = form.name,
            company = form.company,
            title = form.title,
            email = form.email,
            phone = form.phone,
            tags = form.tags.joinToString(","),
            notes = form.notes,
            imagePath = form.imagePath,
            createdAt = now,
            updatedAt = now,
            lastContactAt = null,
            isDeleted = false,
            deletedAt = null,
            syncStatus = SyncStatus.PENDING
        )
        dao.upsertDraft(draft)
        return draft
    }

    suspend fun markStatus(localId: String, status: SyncStatus) {
        dao.updateStatus(localId, status, timeProvider.nowIso())
    }

    suspend fun loadPendingOnce(): List<ContactDraftEntity> =
        dao.getDraftsOnce(listOf(SyncStatus.PENDING, SyncStatus.FAILED))

    fun observeContacts(query: ContactQuery): Flow<List<ContactRecord>> =
        contactsFlow.map { list ->
            list.asSequence()
                .filter { record ->
                    val matchSearch = query.search.isNullOrBlank() || listOfNotNull(
                        record.name,
                        record.company,
                        record.email,
                        record.phone,
                        record.tags?.joinToString(",")
                    ).any { it.contains(query.search.orEmpty(), ignoreCase = true) }
                    val matchDeleted = query.includeDeleted || !record.isDeleted
                    matchSearch && matchDeleted
                }
                .sortedWith(
                    when (query.sortBy) {
                        SortBy.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
                        SortBy.CREATED_AT -> compareBy { it.createdAt }
                    }
                )
                .toList()
        }

    suspend fun refreshFromRemote(includeDeleted: Boolean) {
        val fetched = mutableListOf<ContactRecord>()
        var token: String? = null
        do {
            val page = sheetsReader.fetchPage(token, DEFAULT_PAGE_SIZE, includeDeleted)
            fetched.addAll(page.records)
            token = page.nextPageToken
        } while (token != null)
        contactsFlow.value = fetched
    }

    suspend fun updateContact(updated: ContactRecord) {
        val now = timeProvider.nowIso()
        val newRecord = updated.copy(updatedAt = now)
        contactsFlow.value = contactsFlow.value.map { if (it.id == newRecord.id) newRecord else it }
        sheetsClient.update(newRecord)
    }

    suspend fun softDelete(id: String) {
        val now = timeProvider.nowIso()
        val target = contactsFlow.value.firstOrNull { it.id == id } ?: return
        val deleted = target.copy(isDeleted = true, deletedAt = now, updatedAt = now)
        contactsFlow.value = contactsFlow.value.map { if (it.id == id) deleted else it }
        sheetsClient.update(deleted)
    }

    suspend fun processDraft(draft: ContactDraftEntity): Boolean {
        markStatus(draft.localId, SyncStatus.UPLOADING)
        val uploadResult = draft.imagePath?.let { path ->
            driveClient.upload(path, "${draft.generatedId}.jpg")
        } ?: DriveUploadResult.Success(sharedUrl = "")

        val sharedUrl = when (uploadResult) {
            is DriveUploadResult.Success -> uploadResult.sharedUrl
            is DriveUploadResult.Failure -> {
                telemetry.recordSyncFailure(uploadResult.reason)
                markStatus(draft.localId, SyncStatus.FAILED)
                return false
            }
        }

        val record = draft.asRecord(sharedUrl = sharedUrl.ifEmpty { null })
        val writeResult = sheetsClient.append(record.copy(source = Source.SCAN))
        return when (writeResult) {
            is SheetsWriteResult.Success -> {
                markStatus(draft.localId, SyncStatus.SYNCED)
                true
            }
            is SheetsWriteResult.Failure -> {
                telemetry.recordSyncFailure(writeResult.reason)
                markStatus(draft.localId, SyncStatus.FAILED)
                false
            }
        }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
