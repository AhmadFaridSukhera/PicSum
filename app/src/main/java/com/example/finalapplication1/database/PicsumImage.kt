package com.example.finalapplication1.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "images")
data class PicsumImage(
    @PrimaryKey val id: String,
    val author: String,
    val source: String = "search",
    val isFavorite: Boolean = false,
    val download_url: String
)
@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PicsumImage)
    @Query("SELECT * FROM images WHERE isFavorite = 1")
    fun getFavoriteImages(): Flow<List<PicsumImage>>

    @Query("SELECT EXISTS(SELECT 1 FROM images WHERE id = :id)")
    fun isFavorite(id: String): Boolean

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImageById(imageId: String)
}