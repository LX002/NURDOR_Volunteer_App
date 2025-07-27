package com.example.rma_nurdor_project_v2.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)]
)
data class Volunteer(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val surname: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val username: String,
    val password: String,
    val profilePicture: String?,
    val nearestCity: String,
    val volunteerRole: Int
)
