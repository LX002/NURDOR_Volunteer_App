package com.example.nurdor_volunteer_app_v3.model

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
