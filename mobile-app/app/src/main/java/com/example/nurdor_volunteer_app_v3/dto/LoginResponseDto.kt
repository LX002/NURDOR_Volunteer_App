package com.example.nurdor_volunteer_app_v3.dto

data class LoginResponseDto(
    val volunteerId: Int,
    val accessToken: String,
    val tokenType: String?)