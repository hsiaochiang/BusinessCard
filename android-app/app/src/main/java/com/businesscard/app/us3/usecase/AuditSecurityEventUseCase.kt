package com.businesscard.app.us3.usecase

import com.businesscard.app.telemetry.Telemetry

class AuditSecurityEventUseCase(
    private val telemetry: Telemetry
) {
    fun record(event: String) {
        telemetry.recordAuthEvent(event)
    }
}
