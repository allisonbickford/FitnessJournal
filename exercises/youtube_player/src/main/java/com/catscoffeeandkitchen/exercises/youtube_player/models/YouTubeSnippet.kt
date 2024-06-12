package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: YouTubeThumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String? = null,
    val publishTime: String
)
