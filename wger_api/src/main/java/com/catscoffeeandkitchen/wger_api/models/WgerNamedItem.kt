package com.catscoffeeandkitchen.wger_api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerNamedItem(
    val id: Int,
    val name: String
)
