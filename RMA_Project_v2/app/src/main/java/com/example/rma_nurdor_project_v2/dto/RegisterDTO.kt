package com.example.rma_nurdor_project_v2.dto

data class RegisterDTO(
    val id: Int,
    val name: String,
    val surname: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val username: String,
    val password: String,
    val profilePicture: String,
    val nearestCity: String?,
    val volunteerRole: Int?
)
