package com.businesscard.app.sync

import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.model.ContactRecord

class SyncCoordinator(
    private val repository: ContactRepository
) {
    suspend fun refresh(includeDeleted: Boolean = false) {
        repository.refreshFromRemote(includeDeleted = includeDeleted)
    }

    suspend fun mergeRemote(local: List<ContactRecord>, remote: List<ContactRecord>): List<ContactRecord> {
        val merged = mutableMapOf<String, ContactRecord>()
        (local + remote).forEach { record ->
            val existing = merged[record.id]
            if (existing == null || existing.updatedAt < record.updatedAt) {
                merged[record.id] = record
            }
        }
        return merged.values.toList()
    }
}
