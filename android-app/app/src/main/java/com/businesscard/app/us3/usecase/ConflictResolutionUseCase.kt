package com.businesscard.app.us3.usecase

import com.businesscard.app.model.ContactRecord
import com.businesscard.app.us3.security.ConsistencyPolicy

class ConflictResolutionUseCase(
    private val policy: ConsistencyPolicy
) {
    fun resolve(local: ContactRecord, remote: ContactRecord): ContactRecord =
        if (policy.shouldOverride(local.updatedAt, remote.updatedAt)) remote else local
}
