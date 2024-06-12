package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerMuscleItem(
    val id: Int,
    @Json(name = "name") val latinName: String? = null,
    @Json(name = "name_en") val englishName: String? = null,
    @Json(name = "is_front") val isFront: Boolean? = null,
    @Json(name = "image_url_main") val imageUrlMain: String? = null,
    @Json(name = "image_url_secondary") val imageUrlSecondary: String? = null
)
