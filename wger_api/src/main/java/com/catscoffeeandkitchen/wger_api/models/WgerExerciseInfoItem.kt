package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerExerciseInfoItem(
    val id: Int,
    val uuid: String,
    val created: String? = null,
    @Json(name = "last_update") val lastUpdate: String? = null,
    @Json(name = "last_update_global") val lastUpdateGlobal: String? = null,
    val category: WgerNamedItem? = null,
    val muscles: List<WgerMuscleItem> = emptyList(),
    @Json(name = "muscles_secondary") val musclesSecondary: List<WgerMuscleItem> = emptyList(),
    val equipment: List<WgerNamedItem>? = null,
    val license: WgerLicense? = null,
    @Json(name = "license_author") val licenseAuthor: String? = null,
    val images: List<WgerExerciseImage> = emptyList(),
    val exercises: List<WgerInnerExercise>? = null,
    val variations: Int? = null,
    val videos: List<Any> = emptyList(),
    val comments: List<Any> = emptyList(),
    @Json(name = "author_history") val authorHistory: List<String>? = null,
    @Json(name = "total_authors_history") val totalAuthorsHistory: List<String>? = null,
)
