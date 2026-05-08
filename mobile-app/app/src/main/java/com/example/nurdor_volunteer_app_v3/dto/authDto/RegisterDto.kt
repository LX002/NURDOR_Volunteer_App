package com.example.nurdor_volunteer_app_v3.dto.authDto

data class RegisterDto(
    val name: String,
    val surname: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val username: String,
    val password: String,
    val profilePicture: String?,
    val zipCode: String,
    val volunteerRole: Int
)
