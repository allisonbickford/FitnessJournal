package com.catscoffeeandkitchen.ui.list

import com.catscoffeeandkitchen.models.Goal


class MusclesWorkedDescriptor(
    private val sets: List<Goal>
) {

    /**
     * Anterior Delts, Anterior Deltoids
    Biceps
        Hamstrings
        Brachialis
        Calves
        Glutes, Gluteus Maximus
        Lats, Latissimus dorsi
        Obliques
        Pecs, Chest
        Quads
        Abs
        Serratus Anterior
        Soleus
        Traps
        Triceps
     */
//    val back: List<String> = listOf(
//        "Anterior Delts",
//        "Traps"
//    )
//    val legs: List<String> = listOf(
//        "Hamstrings",
//        "Quads",
//        "Calves",
//        "Glutes",
//    )
//    val upperBody: List<String> = listOf(
//        "Biceps"
//    )
//    val push: List<String> = listOf(
//        "Triceps",
//        "Pecs",
//        "Chest"
//    )
//    val pull: List<String> = listOf(
//        "Lats"
//    )

    val duplicateMuscleNames = mapOf(
        "Glutes" to listOf("Gluteus Maximus"),
        "Pecs" to listOf("Chest")
    )

    private val compounds = listOf(
        "Squat",
        "Deadlift",
        "Bench Press",
    )

    val mostCommonMuscleWorked: String
        get() {
            val muscles = sets
                .filter { it.exercise != null }
                .flatMap { it.exercise!!.musclesWorked }
                .filterNot { duplicateMuscleNames
                    .any { duplicates -> duplicates.value.contains(it) }
                }
                .groupBy { it }
                .map { it.key to it.value.size }
            val max = muscles.maxByOrNull { it.second }
            val results = muscles.filter { it.second == max?.second }

            if (results.size > 3) {
                return "full body"
            }
            return results.joinToString(", ") { it.first }
        }

    val compoundMovements: String = sets.mapNotNull { it.exercise?.name }
        .filter { compounds.any { comp -> it.contains(comp, ignoreCase = true) } }
        .joinToString(", ")
}
