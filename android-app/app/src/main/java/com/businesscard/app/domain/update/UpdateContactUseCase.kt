package com.businesscard.app.domain.update

import com.businesscard.app.data.repository.ContactRepository
import com.businesscard.app.model.ContactRecord

class UpdateContactUseCase(
    private val repository: ContactRepository
) {
    suspend fun update(record: ContactRecord) {
        repository.updateContact(record)
    }

    suspend fun softDelete(id: String) {
        repository.softDelete(id)
    }
}
