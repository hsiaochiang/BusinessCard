package com.businesscard.app.fakes

import com.businesscard.app.data.remote.sheets.SheetsClient
import com.businesscard.app.data.remote.sheets.SheetsWriteResult
import com.businesscard.app.model.ContactRecord

class FakeSheetsClient : SheetsClient {
    val appended = mutableListOf<ContactRecord>()

    override suspend fun append(record: ContactRecord): SheetsWriteResult {
        appended.add(record)
        return SheetsWriteResult.Success
    }
}
