package com.businesscard.app.di

import android.content.Context
import androidx.room.Room
import com.businesscard.app.core.IdGenerator
import com.businesscard.app.core.TimeProvider
import com.businesscard.app.data.db.ContactDatabase
import com.businesscard.app.data.remote.drive.DriveUploader
import com.businesscard.app.data.remote.sheets.SheetsReader
import com.businesscard.app.data.remote.sheets.SheetsWriter
import com.businesscard.app.data.remote.sheets.StubSheetsReader
import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.sync.UploadWorkerFactory
import com.businesscard.app.telemetry.NoopTelemetry

class AppContainer(context: Context) {
    private val telemetry = NoopTelemetry
    private val database = Room.databaseBuilder(
        context.applicationContext,
        ContactDatabase::class.java,
        "contacts.db"
    ).build()

    private val idGenerator = IdGenerator()
    private val timeProvider = TimeProvider()
    private val driveClient = DriveUploader(telemetry)
    private val sheetsClient = SheetsWriter(telemetry)
    private val sheetsReader: SheetsReader = StubSheetsReader()

    val contactRepository: ContactRepository by lazy {
        ContactRepository(
            dao = database.contactDao(),
            driveClient = driveClient,
            sheetsClient = sheetsClient,
            sheetsReader = sheetsReader,
            idGenerator = idGenerator,
            timeProvider = timeProvider,
            telemetry = telemetry
        )
    }

    val workerFactory: UploadWorkerFactory by lazy {
        UploadWorkerFactory(contactRepository)
    }
}
