package com.catscoffeeandkitchen.wger_api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.catscoffeeandkitchen.wger_api.models.ExerciseResult
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseCategory
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseInfoItem
import com.catscoffeeandkitchen.wger_api.models.WgerExerciseItem
import com.catscoffeeandkitchen.wger_api.models.WgerInnerExercise
import com.catscoffeeandkitchen.wger_api.models.WgerMuscle
import me.xdrop.fuzzywuzzy.FuzzySearch
import retrofit2.HttpException
import java.io.IOException
import java.time.OffsetDateTime

class WgerPagingSource(
    private val query: String,
    private val muscleQuery: String,
    private val categoryQuery: String,
    private val service: ExerciseSearchService
): PagingSource<Int, ExerciseResult>() {

    inner class SearchedExerciseData(
        val nextPage: String? = null,
        val exercises: List<ExerciseResult>,
    )

    companion object {
        const val STARTING_PAGE = 0
        const val PAGE_SIZE = 50
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExerciseResult> {
        val pageIndex = params.key ?: STARTING_PAGE
        return try {
            val exercises = getExercises(pageIndex)

            val prevKey = if (pageIndex == STARTING_PAGE) null else pageIndex - 1
            val nextKey = if (exercises.nextPage == null) null else pageIndex + 1

            LoadResult.Page(
                data = exercises.exercises,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    /**
     * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
     */
    override fun getRefreshKey(state: PagingState<Int, ExerciseResult>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private suspend fun getExercises(
        page: Int
    ): SearchedExerciseData {
        val exerciseResult = service.getExercises(
            muscle = WgerMuscle.entries
                .find { it.coloquial.equals(muscleQuery, ignoreCase = true) },
            category = WgerExerciseCategory.entries
                .find { it.name.equals(categoryQuery, ignoreCase = true) },
            limit = PAGE_SIZE,
            offset = page * PAGE_SIZE
        )

        return SearchedExerciseData(
            nextPage = exerciseResult.next,
            exercises = exerciseResult.results.flatMap { item ->
                item.exercises.orEmpty()
                    .filter { query.isBlank() || FuzzySearch.partialRatio(it.name, query) > 60 }
                    .map {
                        it.toExerciseResult(
                            item,
                            variations = item.exercises.orEmpty().map { variant ->
                                variant.toExerciseResult(item, emptyList())
                            }
                        )
                    }
            }
        )
    }

    private fun WgerInnerExercise.toExerciseResult(
        base: WgerExerciseInfoItem,
        variations: List<ExerciseResult>
    ): ExerciseResult {
        return ExerciseResult(
            id = id,
            name = name,
            category = base.category?.let { baseCategory ->
                    WgerExerciseCategory.entries.firstOrNull { cat ->
                        baseCategory.name.equals(cat.name, ignoreCase = true)
                    }
                } ?: WgerExerciseCategory.Unknown,
            imageUrl = base.images.firstOrNull { it.isMain }?.image,
            muscles = base.muscles
                .mapNotNull { it.englishName }
                .filter { it.isNotBlank() },
            musclesSecondary = base.musclesSecondary.mapNotNull { it.englishName },
            aliases = aliases.map { it.alias },
            creationDate = OffsetDateTime.parse(base.created),
            description = description,
            equipment = base.equipment.orEmpty().map { it.name },
            exerciseBase = base.id,
            variations = variations,
            language = language,
            license = license,
            licenseAuthor = base.licenseAuthor,
            uuid = base.uuid
        )
    }
}