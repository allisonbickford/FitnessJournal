package com.catscoffeeandkitchen.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExercisePage
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.wger_api.ExerciseSearchService
import com.catscoffeeandkitchen.wger_api.WgerPagingSource
import com.catscoffeeandkitchen.wger_api.models.ExerciseResult
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseInfoItem
import com.catscoffeeandkitchen.wger_api.models.WgerInnerExercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WgerDataSource @Inject constructor(
    private val exerciseSearchService: ExerciseSearchService
) {
    suspend fun searchExercises(
        search: String,
        muscles: List<String>,
        category: String,
        limit: Int,
        offset: Int
    ): ExercisePage {
        val page = exerciseSearchService.searchExercises(
            search = search,
            muscles = muscles,
            category = category,
            limit = limit,
            offset = offset
        )

        return ExercisePage(
            hasMore = !page.next.isNullOrBlank(),
            exercises = page.results.flatMap { base ->
                base.exercises.orEmpty().map { innerExercise ->
                    innerExercise.toExercise(
                        base = base,
                        variations = base.exercises.orEmpty()
                            .filter { it.id != innerExercise.id }
                            .map { it.toExercise(base) }
                    )
                }
            }
        )
    }

    fun searchPagedExercise(
        search: String,
        muscle: String,
        category: String
    ): Flow<PagingData<Exercise>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            WgerPagingSource(
                search, muscle, category, exerciseSearchService
            )
        }
    ).flow.map { paging ->
        paging.map { exercise ->
            exercise.toExercise(exercise.variations)
        }
    }

    private fun ExerciseResult.toExercise(
        variants: List<ExerciseResult>
    ): Exercise {
        return Exercise(
            id = id.toLong(),
            name = name,
            aliases = aliases,
            musclesWorked = muscles.filter { it.isNotBlank() }
                    + musclesSecondary.filter { it.isNotBlank() },
            category = MuscleCategory.entries.firstOrNull { it.name == category?.name },
            variations = variants.map { it.toExercise(emptyList()) },
            instructions = description,
            imageUrl = imageUrl,
            equipment = equipment.mapNotNull {
                EquipmentType.entries.firstOrNull { type -> type.name == it }
            }
        )
    }

    private fun WgerInnerExercise.toExercise(
        base: WgerExerciseInfoItem,
        variations: List<Exercise> = emptyList()
    ): Exercise {
        return Exercise(
            id = id.toLong(),
            name = name,
            aliases = aliases.map { it.alias },
            musclesWorked = (base.muscles + base.musclesSecondary)
                .mapNotNull { it.englishName }
                .filter { it.isNotBlank() },
            category = MuscleCategory.entries.firstOrNull { it.name == base.category?.name },
            variations = variations,
            instructions = description,
            imageUrl = base.images.firstOrNull { it.isMain }?.image,
            equipment = base.equipment.orEmpty().mapNotNull {
                EquipmentType.entries.firstOrNull { type -> type.name == it.name }
            }
        )
    }
}