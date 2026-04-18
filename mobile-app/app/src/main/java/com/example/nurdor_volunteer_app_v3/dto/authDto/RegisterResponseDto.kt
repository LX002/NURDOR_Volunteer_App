package com.example.nurdor_volunteer_app_v3.dto.authDto

data class RegisterResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
    val message: String
)