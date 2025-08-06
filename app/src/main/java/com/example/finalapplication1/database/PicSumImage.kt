package com.example.finalapplication1.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "images")
data class PicSumImage(
    val id: String = "",
    val author: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val source: String = "",
    val isFavorite: Boolean = false,
    @PrimaryKey val download_url: String = ""
)

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PicSumImage)

    @Query("SELECT * FROM images WHERE isFavorite = 1")
    fun getFavoriteImages(): Flow<List<PicSumImage>>

    @Query("DELETE FROM images WHERE download_url = :downloadUrl")
    suspend fun deleteImageByUrl(downloadUrl: String)
}