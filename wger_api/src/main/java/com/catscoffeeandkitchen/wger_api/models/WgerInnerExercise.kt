package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerInnerExercise(
    val id: Int,
    val uuid: String,
    val name: String,
    @Json(name = "exercise_base") val exerciseBase: Int? = null,
    val description: String? = null,
    val created: String? = null,
    val language: Int = 0,
    val aliases: List<WgerAlias> = emptyList(),
    val notes: List<Any>? = null,
    val license: Int = 0,
    @Json(name = "license_title") val licenseTitle: String? = null,
    @Json(name = "license_object_url") val licenseObjectUrl: String? = null,
    @Json(name = "license_author") val licenseAuthor: String? = null,
    @Json(name = "license_author_url") val licenseAuthorUrl: String? = null,
    @Json(name = "license_derivative_source_url") val licenseDerivativeSourceUrl: String? = null,
    @Json(name = "author_history") val authorHistory: List<String>? = null
)
