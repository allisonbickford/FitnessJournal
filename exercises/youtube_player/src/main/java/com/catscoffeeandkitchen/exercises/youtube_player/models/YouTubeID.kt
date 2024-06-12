package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeID(
    val kind: String,
    val videoId: String
)
