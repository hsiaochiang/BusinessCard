package com.businesscard.app.data.repository

import com.businesscard.app.core.IdGenerator
import com.businesscard.app.core.TimeProvider
import com.businesscard.app.data.db.ContactDao
import com.businesscard.app.data.remote.drive.DriveClient
import com.businesscard.app.data.remote.drive.DriveUploadResult
import com.businesscard.app.data.remote.sheets.SheetsClient
import com.businesscard.app.data.remote.sheets.SheetsWriteResult
import com.businesscard.app.model.ContactDraftEntity
import com.businesscard.app.model.Source
import com.businesscard.app.model.SyncStatus
import com.businesscard.app.telemetry.Telemetry
import java.util.UUID

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

class ContactRepository(
    private val dao: ContactDao,
    private val driveClient: DriveClient,
    private val sheetsClient: SheetsClient,
    private val idGenerator: IdGenerator,
    private val timeProvider: TimeProvider,
    private val telemetry: Telemetry
) {

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
}
