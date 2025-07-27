package com.example.rma_nurdor_project_v2.dto

data class EventsLogDto(
    val id: Int,
    val volunteer: Int,
    val event: Int,
    val isPresent: Byte,
    val note: String?
)
