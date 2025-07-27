package com.example.rma_nurdor_project_v2.model

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
