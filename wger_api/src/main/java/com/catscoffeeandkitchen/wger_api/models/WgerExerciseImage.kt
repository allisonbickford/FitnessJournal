package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerExerciseImage(
    val id: Int,
    val uuid: String,
    @Json(name = "exercise_base") val exerciseBase: Int,
    val image: String,
    @Json(name = "is_main") val isMain: Boolean,
    val style: String,
    val license: Int,
    @Json(name = "license_title") val licenseTitle: String? = null,
    @Json(name = "license_object_url") val licenseObjectUrl: String? = null,
    @Json(name = "license_author") val licenseAuthor: String? = null,
    @Json(name = "license_author_url") val licenseAuthorUrl: String? = null,
    @Json(name = "license_derivative_source_url") val licenseDerivativeUrl: String? = null,
    @Json(name = "author_history") val authorHistory: List<Any>
)
