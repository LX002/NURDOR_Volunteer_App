package com.example.nurdor_volunteer_app_v3.utils

import androidx.room.TypeConverter
import java.time.LocalDateTime

class DateTimeConverter {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }
}