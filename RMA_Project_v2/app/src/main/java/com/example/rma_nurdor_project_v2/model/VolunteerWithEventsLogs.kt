package com.example.rma_nurdor_project_v2.model

import androidx.room.Embedded
import androidx.room.Relation

data class VolunteerWithEventsLogs(
    @Embedded val volunteer: Volunteer,
    @Relation(
        parentColumn = "idVolunteer",
        entityColumn = "volunteer"
    )
    val eventsLogs: List<EventsLog>
)
