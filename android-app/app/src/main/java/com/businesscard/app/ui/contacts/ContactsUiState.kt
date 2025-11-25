package com.businesscard.app.ui.contacts

import com.businesscard.app.model.ContactRecord
import com.businesscard.app.us3.auth.PermissionDecision
import com.businesscard.app.us3.security.ConsistencyDecision

enum class PermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

enum class ConsistencyStatus {
    IDLE,
    CHECKING,
    APPLYING_LOCAL,
    APPLYING_REMOTE,
    ERROR
}

data class ContactsUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.UNKNOWN,
    val consistencyStatus: ConsistencyStatus = ConsistencyStatus.IDLE,
    val lastDecision: ConsistencyDecision? = null,
    val conflictLocal: ContactRecord? = null,
    val conflictRemote: ContactRecord? = null,
    val errorMessage: String? = null
)
