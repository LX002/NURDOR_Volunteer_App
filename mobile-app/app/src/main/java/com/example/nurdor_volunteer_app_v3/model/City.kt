package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey val zipCode: String,
    val cityName: String
) {
    override fun toString(): String {
        return cityName
    }
}
