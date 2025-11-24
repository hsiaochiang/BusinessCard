package com.businesscard.app.data.remote.sheets

class StubSheetsReader : SheetsReader {
    override suspend fun fetchPage(
        pageToken: String?,
        pageSize: Int,
        includeDeleted: Boolean
    ): SheetsPage = SheetsPage(records = emptyList(), nextPageToken = null)
}
