package com.businesscard.app

import android.app.Application
import androidx.work.Configuration
import com.businesscard.app.di.AppContainer
import com.businesscard.app.sync.UploadWorkerFactory

class BusinessCardApp : Application(), Configuration.Provider {
    val appContainer: AppContainer by lazy { AppContainer(this) }

    override fun getWorkManagerConfiguration(): Configuration {
        val factory: UploadWorkerFactory = appContainer.workerFactory
        return Configuration.Builder()
            .setWorkerFactory(factory)
            .build()
    }
}
