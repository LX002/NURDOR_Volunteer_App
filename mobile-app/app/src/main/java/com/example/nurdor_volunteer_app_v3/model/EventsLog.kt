package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_log")
data class EventsLog(
    @PrimaryKey(autoGenerate = true) val idEventsLog: Int? = null,
    val volunteer: Int,
    val event: Int,
    var isPresent: Byte,
    val note: String?
)
