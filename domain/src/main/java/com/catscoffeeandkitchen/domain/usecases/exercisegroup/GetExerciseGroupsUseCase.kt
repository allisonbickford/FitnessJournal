package com.catscoffeeandkitchen.domain.usecases.exercisegroup

import com.catscoffeeandkitchen.domain.interfaces.ExerciseRepository
import com.catscoffeeandkitchen.domain.models.ExerciseGroup
import com.catscoffeeandkitchen.domain.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

class GetExerciseGroupsUseCase @Inject constructor(
    private val repository: ExerciseRepository
) {
    fun run(): Flow<DataState<List<ExerciseGroup>>> = flow {
        emit(DataState.Loading())
        val result = repository.getGroups()
        emit(DataState.Success(result))
    }
        .catch { ex ->
            Timber.e(ex)
            emit(DataState.Error(ex))
        }
        .flowOn(Dispatchers.IO)
    }