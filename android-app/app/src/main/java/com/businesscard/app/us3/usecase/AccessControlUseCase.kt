package com.businesscard.app.us3.usecase

import com.businesscard.app.us3.models.AccessDecision
import com.businesscard.app.us3.models.AccessRequest
import com.businesscard.app.us3.models.AccountSession
import com.businesscard.app.us3.security.PermissionChecker

class AccessControlUseCase(
    private val permissionChecker: PermissionChecker
) {
    fun evaluate(request: AccessRequest, session: AccountSession?): AccessDecision =
        permissionChecker.evaluate(request, session)
}
