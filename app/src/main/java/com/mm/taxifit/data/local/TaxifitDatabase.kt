package com.mm.taxifit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocalUserEntity::class], version = 1, exportSchema = false)
abstract class TaxifitDatabase : RoomDatabase() {
    abstract fun localUserDao(): LocalUserDao

    companion object {
        @Volatile
        private var INSTANCE: TaxifitDatabase? = null

        fun getInstance(context: Context): TaxifitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaxifitDatabase::class.java,
                    "taxifit.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
