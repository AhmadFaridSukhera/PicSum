package com.example.finalapplication1.database

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PicsumApiService {
    // Get list of images
    @GET("v2/list")
    suspend fun getImageList(
        @Query("page") page: Int = 1, @Query("limit") limit: Int = 30
    ): List<PicsumImage>

    @GET("seed/{seed}/info")
    suspend fun getImageInfoBySeed(@Path("seed") seed: String): PicsumImage

}