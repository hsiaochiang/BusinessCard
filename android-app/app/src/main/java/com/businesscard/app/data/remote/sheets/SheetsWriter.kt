package com.businesscard.app.data.remote.sheets

import com.businesscard.app.model.ContactRecord
import com.businesscard.app.telemetry.Telemetry

class SheetsWriter(
    private val telemetry: Telemetry
) : SheetsClient {
    override suspend fun append(record: ContactRecord): SheetsWriteResult {
        // TODO: 實際串接 Sheets API
        telemetry.recordUploadLatency(0)
        return SheetsWriteResult.Success
    }
}
