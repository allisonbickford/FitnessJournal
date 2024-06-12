package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    val default: YouTubeImage,
    val medium: YouTubeImage,
    val high: YouTubeImage,
)
