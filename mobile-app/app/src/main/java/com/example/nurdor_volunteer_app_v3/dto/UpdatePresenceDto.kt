package com.example.nurdor_volunteer_app_v3.dto

data class UpdatePresenceDto(
    val volunteer: Int,
    val event: Int,
    val isPresent: Byte,
    val note: String?
)