package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerLanguage(
    val id: Int,
    @Json(name="short_name") val shortName: String? = null,
    @Json(name="full_name") val fullName: String? = null
)
