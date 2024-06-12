package com.catscoffeeandkitchen.exercises.youtube_player.data

import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeResults
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface YouTubeEndpoints {
    @GET("/youtube/v3/search")
    suspend fun searchVideos(
        @Query("key") key: String,
        @Query("q") search: String,
        @Query("part") part: String = "snippet",
        @Query("maxResults") limit: Int? = null,
        @Query("type") type: String = "video",
    ): YouTubeResults
}