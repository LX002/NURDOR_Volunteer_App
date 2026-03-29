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
    val eventImg: ByteArray?,
    val locationDesc: String?,
    val city: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (idEvent != other.idEvent) return false
        if (isStarted != other.isStarted) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (eventName != other.eventName) return false
        if (description != other.description) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (!eventImg.contentEquals(other.eventImg)) return false
        if (locationDesc != other.locationDesc) return false
        if (city != other.city) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idEvent ?: 0
        result = 31 * result + isStarted
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + eventName.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + (eventImg?.contentHashCode() ?: 0)
        result = 31 * result + (locationDesc?.hashCode() ?: 0)
        result = 31 * result + city.hashCode()
        return result
    }
}
