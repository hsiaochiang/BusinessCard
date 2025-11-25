package com.businesscard.app.us3.security

import com.businesscard.app.us3.models.AccessDecision
import com.businesscard.app.us3.models.AccessRequest
import com.businesscard.app.us3.models.AccountSession

class PermissionChecker {
    fun evaluate(request: AccessRequest, session: AccountSession?): AccessDecision =
        when {
            session == null -> AccessDecision(allowed = false, reason = "session_missing")
            request.scopes.isEmpty() -> AccessDecision(allowed = false, reason = "no_scope_requested")
            else -> AccessDecision(allowed = true)
        }
}
