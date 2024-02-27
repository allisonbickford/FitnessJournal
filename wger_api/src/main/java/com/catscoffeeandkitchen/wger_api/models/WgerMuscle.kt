package com.catscoffeeandkitchen.wger_api.models

enum class WgerMuscle(val number: Int, val coloquial: String) {
    AnteriorDeltoid(number = 2, coloquial = "Anterior Delts"),
    BicepsBrachii(number = 1, coloquial = "Biceps"),
    BicepsFemoris(number = 11, coloquial = "Hamstrings"),
    Brachialis(number = 13, coloquial = "Brachialis"),
    Gastrocnemius(number = 7, coloquial = "Calves"),
    GluteusMaximus(number = 8, coloquial = "Glutes"),
    LatissimusDorsi(number = 12, coloquial = "Lats"),
    ObliquusExternusAbdominis(number = 14, coloquial = "Obliques"),
    PectoralisMajor(number = 4, coloquial = "Pecs"),
    QuadricepsFemoris(number = 10, coloquial = "Quads"),
    RectusAbdominis(number = 6, coloquial = "Abs"),
    SerratusAnterior(number = 3, coloquial = "Serratus Anterior"),
    Soleus(number = 15, coloquial = "Soleus"),
    Trapezius(number = 9, coloquial = "Traps"),
    TricepsBrachii(number = 5, coloquial = "Triceps");
}
