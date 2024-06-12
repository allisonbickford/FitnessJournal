package com.catscoffeeandkitchen.wger_api

import com.catscoffeeandkitchen.wger_api.models.WgerExerciseCategory
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseInfoItem
import com.catscoffeeandkitchen.wger_api.models.WgerInnerExercise
import com.catscoffeeandkitchen.wger_api.models.WgerMuscle
import com.catscoffeeandkitchen.wger_api.models.WgerPage
import me.xdrop.fuzzywuzzy.FuzzySearch
import retrofit2.Retrofit

class ExerciseSearchService(retrofit: Retrofit) {
    private val endpoint = retrofit.create(WgerEndpoints::class.java)

    /**
     * As of 2/20/2024, there is a bug in wger
     * preventing the language filter from working correctly.
     *
     * Once this is resolved, remove the `let` block filtering
     * language manually.
     *
     * https://github.com/wger-project/wger/issues/1119
     */
    suspend fun getExercises(
        limit: Int,
        offset: Int,
        muscles: List<WgerMuscle>?,
        category: WgerExerciseCategory?,
        secondaryMuscles: List<WgerMuscle>? = null
    ): WgerPage<WgerExerciseInfoItem> {
        return endpoint.getExercises(
            limit = limit,
            offset = offset,
            muscles = muscles?.map { it.number }
                ?.joinToString("&muscles="),
            secondaryMuscles = secondaryMuscles?.map { it.number }
                ?.joinToString("&secondary_muscles="),
            category = category?.number
        ).let { page ->
            page.copy(
                results = page.results
                    // Arbitrary filter for quality exercises
                    .filter { it.images.any { image -> image.style.isNotEmpty() } }
                    .map { info ->
                        info.copy(exercises = info.exercises?.filter { it.language == 2 })
                    }
            )
        }
    }

    suspend fun searchExercises(
        search: String,
        muscles: List<String>,
        category: String,
        limit: Int,
        offset: Int
    ): WgerPage<WgerExerciseInfoItem> {
        val page = getExercises(
            muscles = muscles.mapNotNull { muscle ->
                WgerMuscle.entries
                    .find { it.coloquial.equals(muscle, ignoreCase = true) }
            }.takeIf { it.isNotEmpty() },
            category = WgerExerciseCategory.entries
                .find { it.name.equals(category, ignoreCase = true) },
            limit = limit,
            offset = offset
        )

        return page.copy(
            results = page.results.mapNotNull { base ->
                base.copy(
                    exercises = base.exercises.orEmpty()
                        .filter { search.isBlank() || FuzzySearch.partialRatio(it.name, search) > 60 }
                ).takeUnless { base.exercises.isNullOrEmpty() }
            }
        )
    }
}
