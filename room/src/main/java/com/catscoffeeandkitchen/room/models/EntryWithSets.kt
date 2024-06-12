package com.catscoffeeandkitchen.room.models

import androidx.room.Embedded
import androidx.room.Relation
import com.catscoffeeandkitchen.room.entities.SetEntity
import com.catscoffeeandkitchen.room.entities.WorkoutEntryEntity

data class EntryWithSets(
    @Embedded val entry: WorkoutEntryEntity,
    @Relation(
        parentColumn = "weId",
        entityColumn = "entryId",
    )
    val sets: List<SetEntity>?
)
