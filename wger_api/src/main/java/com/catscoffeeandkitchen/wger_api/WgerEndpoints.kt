package com.catscoffeeandkitchen.wger_api

import com.catscoffeeandkitchen.wger_api.models.SearchResponse
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseImage
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseInfoItem
import com.catscoffeeandkitchen.wger_api.models.WgerPage
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WgerEndpoints {
    @Suppress("LongParameterList")
    @GET("/api/v2/exercisebaseinfo")
    @Headers("Accept: application/json")
    suspend fun getExercises(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("muscles") muscles: Int? = null,
        @Query("category") category: Int? = null,
        @Query("language") language: Int = 2, // 2 = english
    ): WgerPage<WgerExerciseInfoItem>

    @GET("/api/v2/exerciseimage")
    @Headers("Accept: application/json")
    suspend fun getExerciseImages(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("exercise_base") id: Int? = null,
    ): WgerPage<WgerExerciseImage>

    @GET("/api/v2/exercise/search")
    @Headers("Accept: application/json")
    suspend fun searchExercises(
        @Query("term") name: String? = null,
        @Query("language") language: Int = 2, // 2 english
    ): SearchResponse
}
