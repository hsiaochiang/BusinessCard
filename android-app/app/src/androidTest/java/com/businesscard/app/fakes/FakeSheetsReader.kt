package com.businesscard.app.fakes

import com.businesscard.app.data.remote.sheets.SheetsPage
import com.businesscard.app.data.remote.sheets.SheetsReader
import com.businesscard.app.model.ContactRecord

class FakeSheetsReader(
    private val backing: MutableList<ContactRecord>
) : SheetsReader {
    override suspend fun fetchPage(
        pageToken: String?,
        pageSize: Int,
        includeDeleted: Boolean
    ): SheetsPage {
        val start = pageToken?.toIntOrNull() ?: 0
        val filtered = if (includeDeleted) backing else backing.filter { !it.isDeleted }
        val slice = filtered.drop(start).take(pageSize)
        val nextStart = start + pageSize
        val nextToken = if (nextStart < filtered.size) nextStart.toString() else null
        return SheetsPage(records = slice, nextPageToken = nextToken)
    }

    fun updateRecord(
        id: String,
        name: String? = null,
        updatedAt: String,
        isDeleted: Boolean? = null
    ) {
        val index = backing.indexOfFirst { it.id == id }
        if (index >= 0) {
            val current = backing[index]
            backing[index] = current.copy(
                name = name ?: current.name,
                updatedAt = updatedAt,
                isDeleted = isDeleted ?: current.isDeleted
            )
        }
    }
}
