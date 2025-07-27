package com.example.rma_nurdor_project_v2.dto

data class EventDto(
    val id: Int,
    val eventName: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val latitude: Double,
    val longitude: Double,
    val eventImg: String?,
    val locationDesc: String?,
    val city: String)
