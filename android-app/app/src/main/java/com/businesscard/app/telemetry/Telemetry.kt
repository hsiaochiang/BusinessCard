package com.businesscard.app.telemetry

interface Telemetry {
    fun recordOcrDuration(millis: Long)
    fun recordUploadLatency(millis: Long)
    fun recordSyncFailure(reason: String)
    fun recordAuthEvent(event: String)
}

object NoopTelemetry : Telemetry {
    override fun recordOcrDuration(millis: Long) = Unit
    override fun recordUploadLatency(millis: Long) = Unit
    override fun recordSyncFailure(reason: String) = Unit
    override fun recordAuthEvent(event: String) = Unit
}
