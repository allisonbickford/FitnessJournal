package com.catscoffeeandkitchen.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.catscoffeeandkitchen.models.EquipmentType
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.MuscleCategory
import com.catscoffeeandkitchen.wger_api.ExerciseSearchService
import com.catscoffeeandkitchen.wger_api.WgerPagingSource
import com.catscoffeeandkitchen.wger_api.models.ExerciseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WgerDataSource @Inject constructor(
    private val exerciseSearchService: ExerciseSearchService
) {
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
            imageUrl = imageUrl,
            equipment = equipment.mapNotNull {
                EquipmentType.entries.firstOrNull { type -> type.name == it }
            }
        )
    }
}