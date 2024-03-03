package com.mkdevelopment.myaccounts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mkdevelopment.myaccounts.common.Constant.DB_NAME


@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class AccountsDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccountsDatabase? = null

        fun getDatabase(context: Context): AccountsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountsDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}