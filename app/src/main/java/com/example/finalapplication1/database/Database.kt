package com.example.finalapplication1.database



import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PicsumImage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}