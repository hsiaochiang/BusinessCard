package com.businesscard.app.us3.viewmodel

import androidx.lifecycle.ViewModel
import com.businesscard.app.model.ContactRecord
import com.businesscard.app.us3.usecase.ConflictResolutionUseCase

class SecurityViewModel(
    private val conflictResolutionUseCase: ConflictResolutionUseCase
) : ViewModel() {
    fun pickLatest(local: ContactRecord, remote: ContactRecord): ContactRecord =
        conflictResolutionUseCase.resolve(local, remote)
}
