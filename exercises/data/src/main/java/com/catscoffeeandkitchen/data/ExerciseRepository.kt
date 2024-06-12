package com.catscoffeeandkitchen.data

import androidx.paging.PagingData
import com.catscoffeeandkitchen.data.ExerciseConverters.toEntity
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExercisePage
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.openai_api.ExerciseAIDataSource
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val roomDataSource: ExerciseRoomDataSource,
    private val wgerDataSource: WgerDataSource,
    private val openAIDataSource: ExerciseAIDataSource
) {
    fun get(id: Long): Flow<Exercise> = roomDataSource.getFlow(id).map { it.toExercise() }

    suspend fun getOrCreateWithName(name: String): Exercise {
        return withContext(Dispatchers.IO) {
            val exercise = roomDataSource.getByName(name)

            if (exercise == null) {
                val id = roomDataSource.create(ExerciseEntity(0L, name = name))
                Exercise(id = id, name = name)
            } else {
                exercise.toExercise()
            }
        }
    }

    suspend fun getOrCreate(exercise: Exercise): Exercise {
        return withContext(Dispatchers.IO) {
            val entity = roomDataSource.getByName(exercise.name)

            if (entity == null) {
                val id = roomDataSource.create(exercise.toEntity())
                exercise.copy(id = id)
            } else {
                entity.toExercise()
            }
        }
    }

    fun getSearchExercisesPager(
        search: String,
        muscle: String,
        category: String
    ): Flow<PagingData<Exercise>> = wgerDataSource.searchPagedExercise(search, muscle, category)

    suspend fun getSearchedExercises(
        search: String,
        muscle: String,
        category: String,
        limit: Int = 50,
        offset: Int = 0
    ): ExercisePage {
        return withContext(Dispatchers.IO) {
            val db = roomDataSource.searchExercises(search, muscle, category, limit, offset)
                .first().map { it.toExercise() }

            val network = wgerDataSource.searchExercises(search, listOf(muscle), category, limit, offset)

            ExercisePage(
                hasMore = network.hasMore,
                exercises = db + network.exercises
            )
        }
    }

    suspend fun getAll(): List<Exercise> {
        return withContext(Dispatchers.IO) {
            roomDataSource.getAll().map { exerciseAndAmount ->
                exerciseAndAmount.exercise.toExercise(
                    amountOfSets = exerciseAndAmount.amountPerformed
                )
            }
        }
    }

    suspend fun getSimilarExercises(exercise: Exercise): List<Exercise> {
        return withContext(Dispatchers.IO) {
            val results = wgerDataSource.searchExercises(
                search = "",
                muscles = exercise.musclesWorked.take(1),
                category = exercise.category?.name.orEmpty(),
                limit = 15,
                offset = 0
            )

            results.exercises
                .filter { it.name != exercise.name && it.category?.name == exercise.category?.name }
                .sortedByDescending { it.musclesWorked.intersect(exercise.musclesWorked.toSet()).size }
                .take(5)
        }
    }

    suspend fun getStats(exercise: Exercise): ExerciseProgressStats {
        return withContext(Dispatchers.IO) {
            ExerciseProgressStats(
                exercise,
                sets = roomDataSource.getSetsForExercise(exercise)
            )
        }
    }

    suspend fun create(exercise: Exercise): Long {
        return withContext(Dispatchers.IO) {
            roomDataSource.create(exercise.toEntity())
        }
    }

    suspend fun update(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            roomDataSource.update(exercise.toEntity())
        }
    }

    suspend fun getMostImprovedExercise(weeksAgo: Int): ExerciseProgressStats? {
        return withContext(Dispatchers.IO) {
            roomDataSource.mostImprovedExercise(weeksAgo)
        }
    }

    suspend fun getExerciseCues(exercise: Exercise): List<String>? {
        return withContext(Dispatchers.IO) {
            openAIDataSource.getExerciseCues(exercise.name)
        }
    }
}
