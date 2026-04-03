package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true) val idEvent: Int? = null,
    val eventName: String,
    val description: String,
    val isStarted: Byte,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val latitude: Double,
    val longitude: Double,
    val eventImg: String?,
    val locationDesc: String?,
    val city: String
)
