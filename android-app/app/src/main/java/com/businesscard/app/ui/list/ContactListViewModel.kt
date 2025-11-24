package com.businesscard.app.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.model.ContactQuery
import com.businesscard.app.model.ContactRecord
import com.businesscard.app.model.SortBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ContactListUiState(
    val search: String = "",
    val sortBy: SortBy = SortBy.NAME,
    val includeDeleted: Boolean = false,
    val contacts: List<ContactRecord> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ContactListViewModel(
    private val repository: ContactRepository
) : ViewModel() {

    private val queryState = MutableStateFlow(ContactQuery())

    val uiState: StateFlow<ContactListUiState> = queryState
        .flatMapLatest { query ->
            repository.observeContacts(query).map { contacts ->
                ContactListUiState(
                    search = query.search.orEmpty(),
                    sortBy = query.sortBy,
                    includeDeleted = query.includeDeleted,
                    contacts = contacts
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ContactListUiState()
        )

    fun setSearch(search: String) {
        queryState.value = queryState.value.copy(search = search)
    }

    fun setSort(sortBy: SortBy) {
        queryState.value = queryState.value.copy(sortBy = sortBy)
    }

    fun toggleIncludeDeleted(include: Boolean) {
        queryState.value = queryState.value.copy(includeDeleted = include)
    }

    fun refresh(includeDeleted: Boolean = false) {
        viewModelScope.launch {
            repository.refreshFromRemote(includeDeleted = includeDeleted)
        }
    }
}
