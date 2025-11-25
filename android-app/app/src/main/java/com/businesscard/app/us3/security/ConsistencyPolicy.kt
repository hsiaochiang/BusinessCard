package com.businesscard.app.us3.security

class ConsistencyPolicy {
    fun shouldOverride(localUpdatedAt: String?, remoteUpdatedAt: String?): Boolean =
        when {
            remoteUpdatedAt == null -> false
            localUpdatedAt == null -> true
            else -> remoteUpdatedAt > localUpdatedAt
        }
}
