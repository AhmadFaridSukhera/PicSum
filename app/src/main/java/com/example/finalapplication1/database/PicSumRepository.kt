package com.example.finalapplication1.database


import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.random.Random


class PicSumRepository @Inject constructor(
    val apiService: PicSumApiService,
    val imageDao: ImageDao
) {

    suspend fun getImageBySeed(): PicSumImage {
        val seed = Random.nextInt(1000, 9999)
        try {
            return apiService.getImageInfoBySeed("$seed")
        } catch (exception: SocketTimeoutException) {
            Log.d("SocketTimeOutException", exception.toString())
            return apiService.getImageInfoBySeed("$seed")
        } catch (exception: Exception) {
            Log.d("Exception", exception.toString())
            return apiService.getImageInfoBySeed("$seed")
        }

    }

    suspend fun getImageList(currentPage: Int): List<PicSumImage> {
        return apiService.getImageList(page = currentPage)
    }

    fun getFavoriteImages(): Flow<List<PicSumImage>> {
        return imageDao.getFavoriteImages()
    }

    suspend fun deleteImageByUrl(downloadUrl: String) {
        return imageDao.deleteImageByUrl(downloadUrl)
    }

    suspend fun insertImage(photo: PicSumImage) {
        return imageDao.insertImage(photo)
    }
}