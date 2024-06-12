package com.catscoffeeandkitchen.exercises.youtube_player.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeResults(
    val kind: String,
    val etag: String,
    val nextPageToken: String? = null,
    val regionCode: String,
    val pageInfo: YouTubePageInfo,
    val items: List<YouTubeSnippetItem>
)
