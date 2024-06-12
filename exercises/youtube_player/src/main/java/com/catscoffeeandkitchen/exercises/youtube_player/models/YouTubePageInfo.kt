package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubePageInfo(
    val totalResults: Long,
    val resultsPerPage: Int
)
