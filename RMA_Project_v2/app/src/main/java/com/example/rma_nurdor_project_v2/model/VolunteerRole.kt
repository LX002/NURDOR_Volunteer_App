package com.example.rma_nurdor_project_v2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VolunteerRole(
    @PrimaryKey val idVolunteerRole: Int,
    val roleName: String
) {
    override fun toString(): String {
        return roleName
    }
}
