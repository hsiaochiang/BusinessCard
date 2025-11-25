package com.businesscard.app.us3.models

data class AccountSession(
    val accountId: String,
    val email: String?,
    val lastAuthAt: String?
)

enum class PermissionScope {
    SHEETS_READ,
    SHEETS_WRITE,
    DRIVE_READ,
    DRIVE_WRITE
}

data class AccessRequest(
    val scopes: Set<PermissionScope>,
    val spreadsheetId: String?,
    val driveFolderId: String?
)

data class AccessDecision(
    val allowed: Boolean,
    val reason: String? = null
)
