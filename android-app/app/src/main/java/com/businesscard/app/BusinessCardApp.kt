package com.businesscard.app

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.businesscard.app.di.AppContainer

class BusinessCardApp : Application(), Configuration.Provider {
    val appContainer: AppContainer by lazy { AppContainer(this) }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}
