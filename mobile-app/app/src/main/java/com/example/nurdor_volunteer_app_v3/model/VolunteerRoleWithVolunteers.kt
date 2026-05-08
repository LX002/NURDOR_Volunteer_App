package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Embedded
import androidx.room.Relation

data class VolunteerRoleWithVolunteers(
    @Embedded val volunteerRole: VolunteerRole,
    @Relation(
        parentColumn = "idVolunteerRole",
        entityColumn = "volunteerRole"
    )
    val volunteers: List<Volunteer>
)
