package com.example.nurdor_volunteer_app_v3.utils

data class VolunteerSearchFilter(
    val idEvent: Int,
    val findBy: String,
    val sortBy: String,
    val searchTxt: String
)