package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Member::class, Transaction::class, AuditLog::class], version = 4, exportSchema = false)
abstract class ChurchDatabase : RoomDatabase() {
    abstract fun churchDao(): ChurchDao

    companion object {
        @Volatile
        private var INSTANCE: ChurchDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE members ADD COLUMN groupName TEXT NOT NULL DEFAULT 'Geral'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE members ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE members ADD COLUMN gender TEXT NOT NULL DEFAULT 'Masculino'")
                db.execSQL("ALTER TABLE members ADD COLUMN isLeader INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `action` TEXT NOT NULL, `details` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `userName` TEXT NOT NULL)")
                db.execSQL("ALTER TABLE members ADD COLUMN birthDate INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): ChurchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChurchDatabase::class.java,
                    "church_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
