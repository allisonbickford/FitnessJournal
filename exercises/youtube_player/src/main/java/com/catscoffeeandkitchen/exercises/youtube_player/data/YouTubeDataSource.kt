package com.catscoffeeandkitchen.exercises.youtube_player.data

import com.catscoffeeandkitchen.exercises.youtube_player.BuildConfig
import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeSnippetItem
import retrofit2.Retrofit
import javax.inject.Inject

class YouTubeDataSource @Inject constructor(
    private val endpoints: YouTubeEndpoints
) {
    suspend fun searchVideos(
        search: String
    ): List<YouTubeSnippetItem> {
        val result = endpoints.searchVideos(
            key = BuildConfig.YOUTUBE_API_KEY,
            search = search
        )

        return result.items
    }
}