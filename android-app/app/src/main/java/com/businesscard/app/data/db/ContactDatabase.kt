package com.businesscard.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.businesscard.app.model.ContactDraftEntity
import com.businesscard.app.model.SyncStatus

@Database(
    entities = [ContactDraftEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(SyncStatusConverter::class)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

class SyncStatusConverter {
    @TypeConverter
    fun toStatus(value: String?): SyncStatus? = value?.let { SyncStatus.valueOf(it) }

    @TypeConverter
    fun fromStatus(status: SyncStatus?): String? = status?.name
}
