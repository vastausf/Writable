package com.vastausf.writable.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vastausf.writable.data.db.dao.DocumentDao
import com.vastausf.writable.data.db.dao.PageDao
import com.vastausf.writable.data.db.entry.DocumentEntity
import com.vastausf.writable.data.db.entry.PageEntity

@Database(
    entities = [
        DocumentEntity::class,
        PageEntity::class,
    ],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun pageDao(): PageDao

    companion object {
        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE documents ADD COLUMN coverColor INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE documents ADD COLUMN edgeColor INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE documents ADD COLUMN bookmarkColor INTEGER NOT NULL DEFAULT -1")
            }
        }
    }
}
