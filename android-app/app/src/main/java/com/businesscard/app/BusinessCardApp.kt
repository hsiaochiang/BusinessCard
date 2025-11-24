package com.businesscard.app

import android.app.Application
import com.businesscard.app.di.AppContainer

class BusinessCardApp : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
