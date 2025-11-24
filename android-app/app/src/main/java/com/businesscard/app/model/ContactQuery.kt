package com.businesscard.app.model

data class ContactQuery(
    val search: String? = null,
    val sortBy: SortBy = SortBy.NAME,
    val includeDeleted: Boolean = false
)

enum class SortBy {
    NAME,
    CREATED_AT
}
