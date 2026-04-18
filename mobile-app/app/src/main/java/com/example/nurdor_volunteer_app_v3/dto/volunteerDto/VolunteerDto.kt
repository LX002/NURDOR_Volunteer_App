package com.example.nurdor_volunteer_app_v3.dto.volunteerDto

data class VolunteerDto(
    val id: Int, 
    val name: String,
    val surname: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val username: String,
    val profilePicture: String,
    val nearestCity: String,
    val volunteerRole: Int,
)