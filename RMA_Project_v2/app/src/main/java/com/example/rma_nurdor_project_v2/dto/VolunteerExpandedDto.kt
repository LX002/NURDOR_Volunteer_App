package com.example.rma_project_demo_v1.dto

data class VolunteerExpandedDto(
    val id: Int,
    val name: String,
    val surname: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val username: String,
    val password: String,
    val profilePicture: String,
    val nearestCity: String?,
    val volunteerRole: Int?
)
