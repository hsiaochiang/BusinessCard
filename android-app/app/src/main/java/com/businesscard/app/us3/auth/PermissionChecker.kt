package com.businesscard.app.us3.auth

import com.businesscard.app.us3.auth.PermissionDecision.DENIED
import com.businesscard.app.us3.auth.PermissionDecision.GRANTED
import com.businesscard.app.us3.auth.PermissionDecision.PERMANENTLY_DENIED
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class PermissionDecision {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

enum class PermissionScope {
    STORAGE,
    DRIVE
}

data class PermissionResult(
    val decision: PermissionDecision,
    val token: String? = null,
    val decidedAt: String = OffsetDateTime.now(ZoneOffset.ofHours(8))
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
)

class PermissionChecker(
    private val fakeTokenProvider: () -> String?,
    private val fakePermissionState: () -> PermissionDecision
) {
    fun check(scopes: Set<PermissionScope>): PermissionResult {
        if (scopes.isEmpty()) {
            return PermissionResult(decision = DENIED, token = null)
        }
        val decision = fakePermissionState.invoke()
        return when (decision) {
            GRANTED -> PermissionResult(decision = GRANTED, token = fakeTokenProvider.invoke())
            DENIED -> PermissionResult(decision = DENIED, token = null)
            PERMANENTLY_DENIED -> PermissionResult(decision = PERMANENTLY_DENIED, token = null)
        }
    }
}
