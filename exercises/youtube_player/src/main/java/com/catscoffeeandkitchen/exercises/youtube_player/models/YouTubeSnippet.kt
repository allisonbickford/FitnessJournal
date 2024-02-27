package com.catscoffeeandkitchen.exercises.youtube_player.models

import java.time.OffsetDateTime

data class YouTubeSnippet(
    val publishedAt: OffsetDateTime?,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelName: String
)
