package com.catscoffeeandkitchen.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.flatMap
import com.catscoffeeandkitchen.data.ExerciseConverters.toEntity
import com.catscoffeeandkitchen.data.ExerciseConverters.toExercise
import com.catscoffeeandkitchen.models.Exercise
import com.catscoffeeandkitchen.models.ExerciseGroup
import com.catscoffeeandkitchen.models.ExerciseProgressStats
import com.catscoffeeandkitchen.room.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val roomDataSource: ExerciseRoomDataSource,
    private val wgerDataSource: WgerDataSource
) {
    fun get(id: Long): Flow<Exercise> = roomDataSource.getFlow(id)

    suspend fun getOrCreate(name: String): Exercise {
        val exercise = roomDataSource.getByName(name)

        if (exercise == null) {
            val id = roomDataSource.create(ExerciseEntity(0L, name = name))
            return Exercise(id = id, name = name)
        }

        return exercise.toExercise()
    }

    fun getSearchExercisesPager(
        search: String,
        muscle: String,
        category: String
    ): Flow<PagingData<Exercise>> {
        return roomDataSource.searchExercises(search, muscle,category)
            .map { PagingData.from(it.map { entity -> entity.toExercise() }) }
            .combine(
                wgerDataSource.searchPagedExercise(search, muscle, category)
            ) { localResults, networkResults ->
                networkResults
            }
    }

    suspend fun getAll(): List<Exercise> {
        return roomDataSource.getAll().map { exerciseAndAmount ->
            exerciseAndAmount.exercise.toExercise(
                amountOfSets = exerciseAndAmount.amountPerformed
            )
        }
    }

    suspend fun create(exercise: Exercise): Long {
        return roomDataSource.create(exercise.toEntity())
    }

    suspend fun update(exercise: Exercise) {
        roomDataSource.update(exercise.toEntity())
    }

    suspend fun getMostImprovedExercise(weeksAgo: Int): ExerciseProgressStats? {
        return roomDataSource.mostImprovedExercise(weeksAgo)
    }
}
