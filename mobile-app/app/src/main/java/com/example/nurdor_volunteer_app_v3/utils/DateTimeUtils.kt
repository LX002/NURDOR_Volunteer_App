package com.example.nurdor_volunteer_app_v3.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeUtils {

    companion object {
        fun changeDateFormat(inputDate: LocalDateTime): String {
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val outputDate = inputDate.format(outputFormatter)
            return outputDate.toString()
        }

        fun calculateDuration(startTime: LocalDateTime, endTime: LocalDateTime): Long {
            return Duration.between(startTime, endTime).seconds / 60
        }
    }
}