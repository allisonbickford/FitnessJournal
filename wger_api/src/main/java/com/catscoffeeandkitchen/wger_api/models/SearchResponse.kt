package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    val suggestions: List<WgerSearchSuggestion>? = null
)
