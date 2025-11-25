package com.businesscard.app.us3

import com.businesscard.app.us3.security.ConsistencyPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsistencyPolicyTest {

    private val policy = ConsistencyPolicy()

    @Test
    fun remoteMissingReturnsFalse() {
        assertFalse(policy.shouldOverride(localUpdatedAt = "2025-01-01", remoteUpdatedAt = null))
    }

    @Test
    fun localMissingReturnsTrue() {
        assertTrue(policy.shouldOverride(localUpdatedAt = null, remoteUpdatedAt = "2025-01-02"))
    }

    @Test
    fun newerRemoteWins() {
        assertTrue(policy.shouldOverride(localUpdatedAt = "2025-01-01", remoteUpdatedAt = "2025-02-01"))
        assertFalse(policy.shouldOverride(localUpdatedAt = "2025-02-01", remoteUpdatedAt = "2025-01-01"))
    }
}
