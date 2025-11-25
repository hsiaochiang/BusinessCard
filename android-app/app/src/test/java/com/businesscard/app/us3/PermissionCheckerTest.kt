package com.businesscard.app.us3

import com.businesscard.app.us3.models.AccessRequest
import com.businesscard.app.us3.models.AccountSession
import com.businesscard.app.us3.models.PermissionScope
import com.businesscard.app.us3.security.PermissionChecker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionCheckerTest {

    private val checker = PermissionChecker()

    @Test
    fun deniesWhenSessionMissing() {
        val decision = checker.evaluate(
            AccessRequest(scopes = setOf(PermissionScope.SHEETS_READ), spreadsheetId = "id", driveFolderId = "folder"),
            session = null
        )
        assertFalse(decision.allowed)
    }

    @Test
    fun deniesWhenNoScope() {
        val session = AccountSession(accountId = "user", email = "user@example.com", lastAuthAt = "2025-01-01")
        val decision = checker.evaluate(
            AccessRequest(scopes = emptySet(), spreadsheetId = "id", driveFolderId = "folder"),
            session = session
        )
        assertFalse(decision.allowed)
    }

    @Test
    fun allowsWhenSessionAndScopePresent() {
        val session = AccountSession(accountId = "user", email = "user@example.com", lastAuthAt = "2025-01-01")
        val decision = checker.evaluate(
            AccessRequest(scopes = setOf(PermissionScope.SHEETS_READ), spreadsheetId = "id", driveFolderId = "folder"),
            session = session
        )
        assertTrue(decision.allowed)
    }
}
