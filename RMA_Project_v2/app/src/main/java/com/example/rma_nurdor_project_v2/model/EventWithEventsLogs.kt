package com.example.rma_nurdor_project_v2.model

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithEventsLogs(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "idEvent",
        entityColumn = "event"
    )
    val eventsLogs: List<EventsLog>
)
