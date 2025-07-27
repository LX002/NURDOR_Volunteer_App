package com.example.rma_nurdor_project_v2.model

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
