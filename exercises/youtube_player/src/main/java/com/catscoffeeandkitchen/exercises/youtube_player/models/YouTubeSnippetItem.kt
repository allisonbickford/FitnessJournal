package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeSnippetItem(
    val kind: String,
    val etag: String,
    val id: YouTubeID,
    val snippet: YouTubeSnippet
)
