package com.example.rma_project_demo_v1.dto

data class CityDto(val zipCode: String, val cityName: String) {
    override fun toString(): String {
        return cityName
    }
}
