package com.example.rma_nurdor_project_v2.dto

data class CityDto(val zipCode: String, val cityName: String) {
    override fun toString(): String {
        return cityName
    }
}
