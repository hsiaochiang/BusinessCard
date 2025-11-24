package com.businesscard.app.core

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class IdGenerator {
    fun generate(): String {
        val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = Random.nextInt(0, 10_000).toString().padStart(4, '0')
        return "$timestamp$random"
    }
}
