package com.example.nurdor_volunteer_app_v3.dto.eventDto

data class CreateEventsLogDto(
    val volunteer: Int,
    val event: Int,
    val isPresent: Byte,
    val note: String?
)