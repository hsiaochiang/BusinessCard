package com.businesscard.app.data.remote.sheets

import com.businesscard.app.model.ContactRecord
import com.businesscard.app.telemetry.Telemetry

class SheetsWriter(
    private val telemetry: Telemetry
) : SheetsClient {
    override suspend fun append(record: ContactRecord): SheetsWriteResult {
        // 待後續串接 Sheets API
        telemetry.recordUploadLatency(DEFAULT_LATENCY_MS)
        return SheetsWriteResult.Success
    }

    private companion object {
        const val DEFAULT_LATENCY_MS = 0L
    }
}
