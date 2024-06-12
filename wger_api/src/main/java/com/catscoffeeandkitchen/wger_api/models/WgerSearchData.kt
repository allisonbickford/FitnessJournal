package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerSearchData(
    val id: Int,
    @Json(name = "base_id") val baseId: Int?,
    val name: String,
    val category: String,
    val image: String?,
    @Json(name = "image_thumbnail") val imageThumbnail: String?,
    val aliases: List<String> = emptyList(),
    val muscles: List<String>? = null,
    val language: Int? = null
)
