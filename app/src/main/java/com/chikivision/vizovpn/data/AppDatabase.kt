package com.chikivision.vizovpn.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ServerEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
}