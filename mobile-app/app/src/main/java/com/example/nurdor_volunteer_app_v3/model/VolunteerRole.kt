package com.example.nurdor_volunteer_app_v3.model

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
