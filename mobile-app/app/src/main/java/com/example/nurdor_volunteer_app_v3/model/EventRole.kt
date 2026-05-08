package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_role")
data class EventRole(
    @PrimaryKey val idEventRole: Int,
    val roleName: String
)
