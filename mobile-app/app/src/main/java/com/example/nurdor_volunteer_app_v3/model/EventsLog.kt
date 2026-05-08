package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events_log",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["idEvent"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["event"])]
)
data class EventsLog(
    @PrimaryKey(autoGenerate = true) val idEventsLog: Int? = null,
    val volunteer: Int,
    val event: Int,
    var isPresent: Byte,
    val note: String?
)
