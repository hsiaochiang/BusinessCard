package com.businesscard.app.data.remote.sheets

import com.businesscard.app.model.ContactRecord

data class SheetsPage(
    val records: List<ContactRecord>,
    val nextPageToken: String?
)

interface SheetsReader {
    suspend fun fetchPage(
        pageToken: String?,
        pageSize: Int,
        includeDeleted: Boolean
    ): SheetsPage
}
