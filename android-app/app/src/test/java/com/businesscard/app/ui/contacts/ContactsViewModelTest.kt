package com.businesscard.app.ui.contacts

import com.businesscard.app.model.ContactRecord
import com.businesscard.app.model.Source
import com.businesscard.app.us3.auth.PermissionChecker
import com.businesscard.app.us3.auth.PermissionDecision
import com.businesscard.app.us3.security.ConsistencyPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakePermissionChecker(
    private val decision: PermissionDecision
) : PermissionChecker(
    fakeTokenProvider = { "fake" },
    fakePermissionState = { decision }
)

private class FakeDataSource(
    var local: ContactRecord? = null,
    var remote: ContactRecord? = null
) : ContactsDataSource {
    var appliedLocal: ContactRecord? = null
    var appliedRemote: ContactRecord? = null
    override suspend fun getLocal(): ContactRecord? = local
    override suspend fun getRemote(): ContactRecord? = remote
    override suspend fun applyLocal(record: ContactRecord) {
        appliedLocal = record
        local = record
    }

    override suspend fun applyRemote(record: ContactRecord) {
        appliedRemote = record
        local = record
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun permissionDeniedUpdatesState() = runTest {
        val vm = ContactsViewModel(
            dataSource = FakeDataSource(),
            permissionChecker = FakePermissionChecker(PermissionDecision.DENIED),
            consistencyPolicy = ConsistencyPolicy()
        )
        vm.onEvent(ContactsUiEvent.RequestDrivePermission)
        assertEquals(PermissionStatus.DENIED, vm.state.value.permissionStatus)
    }

    @Test
    fun permissionGrantedUpdatesState() = runTest {
        val vm = ContactsViewModel(
            dataSource = FakeDataSource(),
            permissionChecker = FakePermissionChecker(PermissionDecision.GRANTED),
            consistencyPolicy = ConsistencyPolicy()
        )
        vm.onEvent(ContactsUiEvent.RequestDrivePermission)
        assertEquals(PermissionStatus.GRANTED, vm.state.value.permissionStatus)
    }

    @Test
    fun consistencyUsesLocalWhenLocalNewer() = runTest {
        val local = ContactRecord(
            id = "1",
            name = "A",
            company = null,
            title = null,
            email = null,
            phone = null,
            tags = null,
            notes = null,
            source = com.businesscard.app.model.Source.MANUAL,
            imageUrl = null,
            createdAt = "2025-01-01T00:00:00+08:00",
            updatedAt = "2025-02-02T00:00:00+08:00",
            lastContactAt = null,
            isDeleted = false,
            deletedAt = null
        )
        val remote = local.copy(updatedAt = "2025-02-01T00:00:00+08:00")
        val ds = FakeDataSource(local = local, remote = remote)
        val vm = ContactsViewModel(
            dataSource = ds,
            permissionChecker = FakePermissionChecker(PermissionDecision.GRANTED),
            consistencyPolicy = ConsistencyPolicy()
        )
        vm.onEvent(ContactsUiEvent.StartConsistencyCheck)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ConsistencyStatus.IDLE, vm.state.value.consistencyStatus)
        assertTrue(ds.appliedLocal != null)
    }

    @Test
    fun consistencyUsesRemoteWhenRemoteNewer() = runTest {
        val local = ContactRecord(
            id = "1",
            name = "A",
            company = null,
            title = null,
            email = null,
            phone = null,
            tags = null,
            notes = null,
            source = com.businesscard.app.model.Source.MANUAL,
            imageUrl = null,
            createdAt = "2025-01-01T00:00:00+08:00",
            updatedAt = "2025-02-01T00:00:00+08:00",
            lastContactAt = null,
            isDeleted = false,
            deletedAt = null
        )
        val remote = local.copy(updatedAt = "2025-02-03T00:00:00+08:00")
        val ds = FakeDataSource(local = local, remote = remote)
        val vm = ContactsViewModel(
            dataSource = ds,
            permissionChecker = FakePermissionChecker(PermissionDecision.GRANTED),
            consistencyPolicy = ConsistencyPolicy()
        )
        vm.onEvent(ContactsUiEvent.StartConsistencyCheck)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ConsistencyStatus.IDLE, vm.state.value.consistencyStatus)
        assertTrue(ds.appliedRemote != null)
    }
}
