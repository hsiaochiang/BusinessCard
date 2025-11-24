package com.businesscard.app.core

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class IdGenerator {
    fun generate(): String {
        val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN))
        val random = Random.nextInt(FROM_RANDOM, UNTIL_RANDOM)
            .toString()
            .padStart(RANDOM_PADDING, '0')
        return "$timestamp$random"
    }

    private companion object {
        const val FROM_RANDOM = 0
        const val UNTIL_RANDOM = 10_000
        const val RANDOM_PADDING = 4
        const val TIMESTAMP_PATTERN = "yyyyMMddHHmmss"
    }
}
