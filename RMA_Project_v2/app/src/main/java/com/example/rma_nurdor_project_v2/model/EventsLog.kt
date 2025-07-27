package com.example.rma_nurdor_project_v2.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "events_log")
data class EventsLog(
    @PrimaryKey(autoGenerate = true) val idEventsLog: Int? = null,
    val volunteer: Int,
    val event: Int,
    var isPresent: Byte,
    val note: String?
)
