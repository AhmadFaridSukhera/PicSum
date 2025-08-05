package com.example.finalapplication1.database



import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PicSumImage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}