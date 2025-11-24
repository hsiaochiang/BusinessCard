package com.businesscard.app.data.remote.sheets

import com.businesscard.app.model.ContactRecord

interface SheetsClient {
    suspend fun append(record: ContactRecord): SheetsWriteResult
    suspend fun update(record: ContactRecord): SheetsWriteResult = append(record)
}

sealed interface SheetsWriteResult {
    data object Success : SheetsWriteResult
    data class Failure(val reason: String) : SheetsWriteResult
}
