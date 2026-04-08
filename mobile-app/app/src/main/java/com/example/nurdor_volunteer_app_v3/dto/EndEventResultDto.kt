package com.example.nurdor_volunteer_app_v3.dto

data class EndEventResultDto(
    val message: String,
    val totalDonations: Long,
    val stands: List<StandDto>
)