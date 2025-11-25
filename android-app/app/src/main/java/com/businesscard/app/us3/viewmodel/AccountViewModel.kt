package com.businesscard.app.us3.viewmodel

import androidx.lifecycle.ViewModel
import com.businesscard.app.us3.models.AccessRequest
import com.businesscard.app.us3.models.AccountSession
import com.businesscard.app.us3.usecase.AccessControlUseCase

class AccountViewModel(
    private val accessControlUseCase: AccessControlUseCase
) : ViewModel() {
    fun canAccess(request: AccessRequest, session: AccountSession?) =
        accessControlUseCase.evaluate(request, session)
}
