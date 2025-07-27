package com.example.rma_nurdor_project_v2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stand(
    @PrimaryKey val idStand: Int,
    val standName: String,
    val totalDonations: Int,
    val event: Int
)
