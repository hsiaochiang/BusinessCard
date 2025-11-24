package com.businesscard.app.domain.create

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.businesscard.app.data.repository.ContactDraftForm
import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.sync.UploadWorker

class CreateContactUseCase(
    private val repository: ContactRepository,
    private val workManager: WorkManager
) {

    suspend fun saveDraft(form: ContactDraftForm) {
        repository.createDraft(form)
        enqueueUpload()
    }

    fun enqueueUploadOnly() {
        enqueueUpload()
    }

    private fun enqueueUpload() {
        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .addTag(UploadWorker.TAG)
            .build()
        workManager.enqueueUniqueWork(
            UploadWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
