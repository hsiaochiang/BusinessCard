package com.businesscard.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businesscard.app.model.ContactDraftEntity
import com.businesscard.app.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDraft(draft: ContactDraftEntity)

    @Update
    suspend fun updateDraft(draft: ContactDraftEntity)

    @Query("DELETE FROM contact_drafts WHERE localId = :localId")
    suspend fun deleteDraft(localId: String)

    @Query("SELECT * FROM contact_drafts WHERE localId = :localId LIMIT 1")
    suspend fun findDraft(localId: String): ContactDraftEntity?

    @Query("SELECT * FROM contact_drafts WHERE syncStatus IN (:status)")
    fun streamDrafts(status: List<SyncStatus>): Flow<List<ContactDraftEntity>>

    @Query("SELECT * FROM contact_drafts WHERE syncStatus IN (:status)")
    suspend fun getDraftsOnce(status: List<SyncStatus>): List<ContactDraftEntity>

    @Query("UPDATE contact_drafts SET syncStatus = :status, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun updateStatus(localId: String, status: SyncStatus, updatedAt: String)
}
