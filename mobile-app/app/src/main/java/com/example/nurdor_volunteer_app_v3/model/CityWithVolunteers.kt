package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Embedded
import androidx.room.Relation

data class CityWithVolunteers(
    @Embedded val city: City,
    @Relation(
        parentColumn = "zipCode",
        entityColumn = "nearestCity"
    )
    val volunteers: List<Volunteer>
)
