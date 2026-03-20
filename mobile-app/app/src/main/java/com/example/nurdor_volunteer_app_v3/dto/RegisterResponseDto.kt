package com.example.nurdor_volunteer_app_v3.dto

import java.time.LocalDateTime

data class RegisterResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val message: String
)