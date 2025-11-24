package com.businesscard.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.businesscard.app.core.IdGenerator
import com.businesscard.app.core.TimeProvider
import com.businesscard.app.data.db.ContactDatabase
import com.businesscard.app.data.remote.drive.DriveUploader
import com.businesscard.app.data.remote.sheets.SheetsWriter
import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.fakes.FakeSheetsClient
import com.businesscard.app.fakes.FakeSheetsReader
import com.businesscard.app.fakes.FakeTelemetry
import com.businesscard.app.model.ContactQuery
import com.businesscard.app.model.ContactRecord
import com.businesscard.app.model.SortBy
import com.businesscard.app.model.Source
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SheetsQueryTest {

    private lateinit var database: ContactDatabase
    private lateinit var repo: ContactRepository
    private lateinit var sheetsClient: FakeSheetsClient
    private lateinit var sheetsReader: FakeSheetsReader

    @Before
    fun setup() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        database = androidx.room.Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val seedData = mutableListOf(
            ContactRecord(
                id = "ID1",
                name = "Alice",
                company = "Alpha",
                title = "Mgr",
                email = "a@example.com",
                phone = "0911",
                tags = listOf("vip"),
                notes = null,
                source = Source.MANUAL,
                imageUrl = "drive://ID1.jpg",
                createdAt = "2025-01-01T00:00:00+08:00",
                updatedAt = "2025-01-01T00:00:00+08:00",
                lastContactAt = null,
                isDeleted = false,
                deletedAt = null
            ),
            ContactRecord(
                id = "ID2",
                name = "Bob",
                company = "Beta",
                title = "Lead",
                email = "b@example.com",
                phone = "0922",
                tags = listOf("partner"),
                notes = null,
                source = Source.MANUAL,
                imageUrl = "drive://ID2.jpg",
                createdAt = "2025-01-02T00:00:00+08:00",
                updatedAt = "2025-01-02T00:00:00+08:00",
                lastContactAt = null,
                isDeleted = false,
                deletedAt = null
            ),
            ContactRecord(
                id = "ID3",
                name = "Carl",
                company = "Gamma",
                title = "Dev",
                email = "c@example.com",
                phone = "0933",
                tags = listOf("old"),
                notes = null,
                source = Source.MANUAL,
                imageUrl = "drive://ID3.jpg",
                createdAt = "2025-01-03T00:00:00+08:00",
                updatedAt = "2025-01-03T00:00:00+08:00",
                lastContactAt = null,
                isDeleted = true,
                deletedAt = "2025-02-01T00:00:00+08:00"
            )
        )
        sheetsReader = FakeSheetsReader(seedData)
        sheetsClient = FakeSheetsClient()
        repo = ContactRepository(
            dao = database.contactDao(),
            driveClient = DriveUploader(FakeTelemetry()),
            sheetsClient = sheetsClient,
            sheetsReader = sheetsReader,
            idGenerator = IdGenerator(),
            timeProvider = TimeProvider(),
            telemetry = FakeTelemetry()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun read_sort_filter_and_soft_delete() = runBlocking {
        repo.refreshFromRemote(includeDeleted = false)
        val initial = repo.observeContacts(ContactQuery(sortBy = SortBy.NAME)).first()
        assertEquals(2, initial.size)
        assertEquals("Alice", initial.first().name)

        // 更新 Bob 名稱並提升 updatedAt，再刷新
        sheetsReader.updateRecord(
            "ID2",
            "Bobby",
            updatedAt = "2025-02-10T00:00:00+08:00",
            isDeleted = false
        )
        repo.refreshFromRemote(includeDeleted = false)
        val afterUpdate = repo.observeContacts(ContactQuery(sortBy = SortBy.NAME)).first()
        assertEquals("Alice", afterUpdate.first().name)
        assertEquals("Bobby", afterUpdate.last().name)

        // 軟刪除 Alice 後刷新，預設不應顯示
        sheetsReader.updateRecord(
            id = "ID1",
            name = "Alice",
            updatedAt = "2025-03-01T00:00:00+08:00",
            isDeleted = true
        )
        repo.refreshFromRemote(includeDeleted = false)
        val afterDelete = repo.observeContacts(ContactQuery()).first()
        assertEquals(1, afterDelete.size)
        assertFalse(afterDelete.any { it.id == "ID1" })

        // includeDeleted 應帶回軟刪除資料
        val withDeleted = repo.observeContacts(ContactQuery(includeDeleted = true)).first()
        assertTrue(withDeleted.any { it.id == "ID1" && it.isDeleted })
    }
}
