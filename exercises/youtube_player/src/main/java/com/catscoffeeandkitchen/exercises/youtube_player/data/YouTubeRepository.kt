package com.catscoffeeandkitchen.exercises.youtube_player.data

import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeSnippet
import javax.inject.Inject

class YouTubeRepository @Inject constructor(
    val dataSource: YouTubeDataSource
) {


    suspend fun searchYouTubeVideos(
        search: String
    ): List<YouTubeSnippet> {
        return emptyList()
    }
}