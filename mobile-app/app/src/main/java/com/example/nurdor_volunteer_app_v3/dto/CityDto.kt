package com.example.nurdor_volunteer_app_v3.dto

data class CityDto(val zipCode: String, val cityName: String) {
    override fun toString(): String {
        return cityName
    }
}
