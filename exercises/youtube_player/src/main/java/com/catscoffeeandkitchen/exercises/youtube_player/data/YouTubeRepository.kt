package com.catscoffeeandkitchen.exercises.youtube_player.data

import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeSnippet
import com.catscoffeeandkitchen.exercises.youtube_player.models.YouTubeVideo
import javax.inject.Inject

class YouTubeRepository @Inject constructor(
    private val dataSource: YouTubeDataSource
) {

    suspend fun searchYouTubeVideos(
        search: String
    ): List<YouTubeVideo> {
        return dataSource.searchVideos(search).map { snippet ->
            YouTubeVideo(
                id = snippet.id.videoId,
                title = snippet.snippet.title,
                channelName = snippet.snippet.channelTitle,
                imageUrl = snippet.snippet.thumbnails.medium.url,
                description = snippet.snippet.description
            )
        }
    }
}