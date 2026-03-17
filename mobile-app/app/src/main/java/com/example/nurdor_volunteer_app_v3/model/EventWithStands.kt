package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithStands(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "idEvent",
        entityColumn = "event"
    )
    val stands: List<Stand>
)
