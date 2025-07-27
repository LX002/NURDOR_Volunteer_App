package com.example.rma_nurdor_project_v2.model

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
