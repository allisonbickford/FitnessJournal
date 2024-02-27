package com.catscoffeeandkitchen.wger_api.models

import java.time.OffsetDateTime

data class ExerciseResult(
    val id: Int,
    val name: String,
    val category: WgerExerciseCategory?,
    val imageUrl: String?,
    val muscles: List<String>,
    val musclesSecondary: List<String>,
    val aliases: List<String>,
    val creationDate: OffsetDateTime,
    val description: String?,
    val equipment: List<String>,
    val exerciseBase: Int?,
    val variations: List<ExerciseResult>,
    val language: Int,
    val license: Int?,
    val licenseAuthor: String?,
    val uuid: String
)
