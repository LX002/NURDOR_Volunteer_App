package com.example.nurdor_volunteer_app_v3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stand(
    @PrimaryKey val idStand: Int,
    val standName: String,
    val totalDonations: Int,
    val event: Int?
)
