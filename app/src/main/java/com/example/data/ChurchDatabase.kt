package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Member::class, Transaction::class], version = 1, exportSchema = false)
abstract class ChurchDatabase : RoomDatabase() {
    abstract fun churchDao(): ChurchDao

    companion object {
        @Volatile
        private var INSTANCE: ChurchDatabase? = null

        fun getDatabase(context: Context): ChurchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChurchDatabase::class.java,
                    "church_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
