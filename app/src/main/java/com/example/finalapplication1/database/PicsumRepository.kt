package com.example.finalapplication1.database


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.random.Random


class PicsumRepository @Inject constructor(val apiService: PicsumApiService, val imageDao: ImageDao) {

    fun getRandomImageUrlWithSeed( width: Int, height: Int): String {
        val seed = Random.nextInt(1000, 9999)
        return "https://picsum.photos/seed/$seed/${width}/${height}"
    }
    suspend fun getImageInfoBySeed(seed: String): PicsumImage {
        return apiService.getImageInfoBySeed(seed)
    }
    suspend fun getImageList(currentPage: Int): List<PicsumImage> {
        return apiService.getImageList(page = currentPage)
    }
    fun getFavoriteImages(): Flow<List<PicsumImage>> {
        return imageDao.getFavoriteImages()
    }
    suspend fun deleteImageById(id: String){
        return imageDao.deleteImageById(id)
    }
    suspend fun insertImage(photo: PicsumImage){
        return imageDao.insertImage(photo)
    }
}