package com.businesscard.app.fakes

import com.businesscard.app.telemetry.Telemetry

class FakeTelemetry : Telemetry {
    val ocrDurations = mutableListOf<Long>()
    val uploadLatencies = mutableListOf<Long>()
    val syncFailures = mutableListOf<String>()
    val authEvents = mutableListOf<String>()

    override fun recordOcrDuration(millis: Long) {
        ocrDurations.add(millis)
    }

    override fun recordUploadLatency(millis: Long) {
        uploadLatencies.add(millis)
    }

    override fun recordSyncFailure(reason: String) {
        syncFailures.add(reason)
    }

    override fun recordAuthEvent(event: String) {
        authEvents.add(event)
    }
}
