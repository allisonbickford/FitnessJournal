package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerAlias(
    val id: Int,
    val uuid: String,
    val alias: String,
)
