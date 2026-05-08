package com.example.nurdor_volunteer_app_v3.model

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
