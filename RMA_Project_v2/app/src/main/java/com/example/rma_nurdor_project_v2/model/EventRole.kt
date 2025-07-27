package com.example.rma_nurdor_project_v2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_role")
data class EventRole(
    @PrimaryKey val idEventRole: Int,
    val roleName: String
)
