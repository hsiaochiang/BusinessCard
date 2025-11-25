package com.businesscard.app.us3

import com.businesscard.app.us3.auth.PermissionChecker
import com.businesscard.app.us3.auth.PermissionDecision
import com.businesscard.app.us3.auth.PermissionScope
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionCheckerTest {

    private val grantedChecker = PermissionChecker(
        fakeTokenProvider = { "fake-token" },
        fakePermissionState = { PermissionDecision.GRANTED }
    )
    private val deniedChecker = PermissionChecker(
        fakeTokenProvider = { null },
        fakePermissionState = { PermissionDecision.DENIED }
    )
    private val permanentChecker = PermissionChecker(
        fakeTokenProvider = { null },
        fakePermissionState = { PermissionDecision.PERMANENTLY_DENIED }
    )

    @Test
    fun deniesWhenSessionMissing() {
        val decision = deniedChecker.check(setOf(PermissionScope.STORAGE))
        assertFalse(decision.decision == PermissionDecision.GRANTED)
    }

    @Test
    fun deniesWhenNoScope() {
        val decision = deniedChecker.check(emptySet())
        assertFalse(decision.decision == PermissionDecision.GRANTED)
    }

    @Test
    fun allowsWhenSessionAndScopePresent() {
        val decision = grantedChecker.check(setOf(PermissionScope.DRIVE))
        assertTrue(decision.decision == PermissionDecision.GRANTED)
        assertTrue(!decision.token.isNullOrBlank())
    }

    @Test
    fun permanentlyDenied() {
        val decision = permanentChecker.check(setOf(PermissionScope.DRIVE))
        assertTrue(decision.decision == PermissionDecision.PERMANENTLY_DENIED)
    }
}
