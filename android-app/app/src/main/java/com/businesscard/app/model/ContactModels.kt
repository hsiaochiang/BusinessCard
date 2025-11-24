package com.businesscard.app.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class ContactRecord(
    val id: String,
    val name: String,
    val company: String?,
    val title: String?,
    val email: String?,
    val phone: String?,
    val tags: List<String>?,
    val notes: String?,
    val source: Source,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastContactAt: String?,
    val isDeleted: Boolean,
    val deletedAt: String?
)

data class ContactImage(
    val fileName: String,
    val driveId: String?,
    val sharedUrl: String?,
    val createdAt: String,
    val ownerAccount: String
)

enum class Source {
    SCAN,
    MANUAL,
    IMPORT
}

enum class SyncStatus {
    PENDING,
    UPLOADING,
    FAILED,
    SYNCED
}

@Entity(tableName = "contact_drafts")
data class ContactDraftEntity(
    @PrimaryKey val localId: String,
    val generatedId: String,
    val name: String,
    val company: String?,
    val title: String?,
    val email: String?,
    val phone: String?,
    val tags: String?,
    val notes: String?,
    val imagePath: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastContactAt: String?,
    val isDeleted: Boolean,
    val deletedAt: String?,
    val syncStatus: SyncStatus
) {
    fun asRecord(sharedUrl: String? = null): ContactRecord = ContactRecord(
        id = generatedId,
        name = name,
        company = company,
        title = title,
        email = email,
        phone = phone,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
        notes = notes,
        source = Source.SCAN,
        imageUrl = sharedUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastContactAt = lastContactAt,
        isDeleted = isDeleted,
        deletedAt = deletedAt
    )
}

data class ContactDraftWithImage(
    @Embedded val draft: ContactDraftEntity,
    val image: ContactImage? = null
)
