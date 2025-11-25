package com.businesscard.app.us3.security

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class ConsistencyDecision {
    USE_REMOTE,
    USE_LOCAL,
    NO_ACTION
}

class ConsistencyPolicy {
    fun decide(localUpdatedAt: String?, remoteUpdatedAt: String?): ConsistencyDecision {
        val remote = remoteUpdatedAt?.let { parseTime(it) }
        val local = localUpdatedAt?.let { parseTime(it) }
        return when {
            remote == null && local != null -> ConsistencyDecision.USE_LOCAL
            remote != null && local == null -> ConsistencyDecision.USE_REMOTE
            remote == null && local == null -> ConsistencyDecision.NO_ACTION
            remote != null && local != null && remote.isAfter(local) -> ConsistencyDecision.USE_REMOTE
            remote != null && local != null && local.isAfter(remote) -> ConsistencyDecision.USE_LOCAL
            else -> ConsistencyDecision.NO_ACTION
        }
    }

    private fun parseTime(value: String): OffsetDateTime =
        OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.ofHours(8)))
}
