package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeImage(
    val url: String,
    val width: Int,
    val height: Int
)
