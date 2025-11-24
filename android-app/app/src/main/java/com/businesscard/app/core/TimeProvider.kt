package com.businesscard.app.core

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TimeProvider(
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
) {
    fun nowIso(): String = OffsetDateTime.now(ZoneOffset.ofHours(DEFAULT_OFFSET_HOURS)).format(formatter)

    private companion object {
        const val DEFAULT_OFFSET_HOURS = 8
    }
}
