package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerExerciseItem(
    @Json(name = "author_history") val authorHistory: List<String>? = null,
    val category: Int? = null,
    @Json(name = "creation_date") val creationDate: String? = null,
    val description: String? = null,
    val equipment: List<Int>? = null,
    @Json(name = "exercise_base") val exerciseBase: Int? = null,
    val id: Int,
    val language: Int? = null,
    val license: Int? = null,
    @Json(name = "license_author") val licenseAuthor: String? = null,
    val muscles: List<Int>,
    @Json(name = "muscles_secondary") val musclesSecondary: List<Int>,
    val name: String,
    val uuid: String,
    val variations: List<Int>
)
