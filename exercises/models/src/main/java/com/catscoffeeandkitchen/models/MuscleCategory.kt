package com.catscoffeeandkitchen.models

enum class MuscleCategory(
    val muscles: List<String>
) {
    Chest(
        muscles = listOf(
            "Chest"
        )
    ),
    Abs(
        muscles = listOf(
            "Abs"
        )
    ),
    Legs(
        muscles = listOf(
            "Hamstrings",
            "Calves",
            "Glutes",
            "Quads"
        )
    ),
    Arms(
        muscles = listOf(
            "Biceps",
            "Triceps"
        )
    ),
    Back(
        muscles = listOf(
            "Lats"
        )
    )
}