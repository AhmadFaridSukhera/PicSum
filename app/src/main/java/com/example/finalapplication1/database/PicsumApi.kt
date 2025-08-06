package com.example.finalapplication1.database

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PicSumApiService {

    @GET("v2/list")
    suspend fun getImageList(
        @Query("page") page: Int = 1, @Query("limit") limit: Int = 40
    ): List<PicSumImage>

    @GET("seed/{seed}/info")
    suspend fun getImageInfoBySeed(@Path("seed") seed: String): PicSumImage

}