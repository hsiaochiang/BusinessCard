package com.businesscard.app.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.businesscard.app.data.repository.ContactRepository

class UploadWorkerFactory(
    private val repository: ContactRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == UploadWorker::class.qualifiedName) {
            UploadWorker(appContext, workerParameters, repository)
        } else {
            null
        }
    }
}
