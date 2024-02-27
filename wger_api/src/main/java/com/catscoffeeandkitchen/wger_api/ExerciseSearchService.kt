package com.catscoffeeandkitchen.wger_api

import com.catscoffeeandkitchen.wger_api.models.WgerExerciseCategory
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseInfoItem
import com.catscoffeeandkitchen.wger_api.models.WgerMuscle
import com.catscoffeeandkitchen.wger_api.models.WgerPage
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
        muscle: WgerMuscle?,
        category: WgerExerciseCategory?,
    ): WgerPage<WgerExerciseInfoItem> {
        return endpoint.getExercises(
            limit = limit,
            offset = offset,
            muscles = muscle?.number,
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
}
