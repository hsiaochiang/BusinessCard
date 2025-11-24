package com.businesscard.app.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.businesscard.app.data.repository.ContactRepository

class UploadWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: ContactRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val drafts = repository.loadPendingOnce()
        if (drafts.isEmpty()) return Result.success()

        var allSuccess = true
        drafts.forEach { draft ->
            val ok = repository.processDraft(draft)
            if (!ok) {
                allSuccess = false
            }
        }
        return if (allSuccess) Result.success() else Result.retry()
    }

    companion object {
        const val WORK_NAME = "contact-upload"
        const val TAG = "contact-upload"
    }
}
