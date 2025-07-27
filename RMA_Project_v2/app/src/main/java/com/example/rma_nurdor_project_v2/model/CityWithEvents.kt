package com.example.rma_nurdor_project_v2.model

import androidx.room.Embedded
import androidx.room.Relation

data class CityWithEvents(
    @Embedded val city: City,
    @Relation(
        parentColumn = "zipCode",
        entityColumn = "city"
    )
    val events: List<Event>
)
