package com.example.nurdor_volunteer_app_v3.dto.errorDto

import java.time.LocalDateTime

data class ErrorEntityDto(
    val message: String,
    val timestamp: LocalDateTime
)