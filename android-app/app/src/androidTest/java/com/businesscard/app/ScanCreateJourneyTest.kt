package com.businesscard.app

import android.graphics.Bitmap
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkInfo
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.businesscard.app.core.IdGenerator
import com.businesscard.app.core.TimeProvider
import com.businesscard.app.data.db.ContactDatabase
import com.businesscard.app.data.remote.drive.DriveUploadResult
import com.businesscard.app.data.remote.sheets.SheetsWriteResult
import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.domain.create.CreateContactUseCase
import com.businesscard.app.fakes.FakeDriveClient
import com.businesscard.app.fakes.FakeOcrProcessor
import com.businesscard.app.fakes.FakeSheetsClient
import com.businesscard.app.fakes.FakeTelemetry
import com.businesscard.app.model.SyncStatus
import com.businesscard.app.sync.UploadWorker
import com.businesscard.app.sync.UploadWorkerFactory
import com.businesscard.app.ui.scan.ImageFileStore
import com.businesscard.app.ui.scan.OcrResult
import com.businesscard.app.ui.scan.ScanViewModel
import com.businesscard.app.ui.scan.ScanViewModelFactory
import com.businesscard.app.ui.scan.ScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScanCreateJourneyTest {

    private lateinit var database: ContactDatabase
    private lateinit var repo: ContactRepository
    private lateinit var drive: FakeDriveClient
    private lateinit var sheets: FakeSheetsClient
    private lateinit var telemetry: FakeTelemetry
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<BusinessCardApp>()
        database = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        drive = FakeDriveClient()
        sheets = FakeSheetsClient()
        telemetry = FakeTelemetry()
        repo = ContactRepository(
            dao = database.contactDao(),
            driveClient = drive,
            sheetsClient = sheets,
            idGenerator = IdGenerator(),
            timeProvider = TimeProvider(),
            telemetry = telemetry
        )
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(UploadWorkerFactory(repo))
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun scan_flow_offline_to_upload_success() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<BusinessCardApp>()
        val testBitmap = Bitmap.createBitmap(TEST_BITMAP_SIZE, TEST_BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val fakeOcr = FakeOcrProcessor(
            OcrResult(
                name = "測試名片",
                company = "測試公司",
                phone = "0912345678"
            )
        )
        val useCase = CreateContactUseCase(repo, workManager)
        val viewModelFactory = ScanViewModelFactory(
            application = context,
            createContactUseCase = useCase,
            ocrProcessor = fakeOcr,
            imageStore = ImageFileStore(context)
        )
        val viewModel = viewModelFactory.create(ScanViewModel::class.java)

        viewModel.onImageCaptured(testBitmap)

        val uploadedState = viewModel.uiState
            .filterIsInstance<ScanUiState.Uploaded>()
            .first()
        assertTrue(uploadedState is ScanUiState.Uploaded)

        // 驗證暫存狀態與 WorkManager 執行結果
        val drafts = repo.loadPendingOnce()
        assertTrue(drafts.isEmpty() || drafts.all { it.syncStatus != SyncStatus.FAILED })

        // 等待 WorkManager 將唯一工作完成
        val works = workManager.getWorkInfosForUniqueWork(UploadWorker.WORK_NAME).get()
        val finished = works.all { it.state == WorkInfo.State.SUCCEEDED }
        assertTrue(finished)

        // 驗證 fake 服務被呼叫
        assertEquals(1, drive.uploads.size)
        assertEquals(1, sheets.appended.size)
        val appended = sheets.appended.first()
        assertEquals("測試名片", appended.name)
        assertEquals("drive://${appended.id}.jpg", appended.imageUrl)
    }

    private companion object {
        const val TEST_BITMAP_SIZE = 10
    }
}
