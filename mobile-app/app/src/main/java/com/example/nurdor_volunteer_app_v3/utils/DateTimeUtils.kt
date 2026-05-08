package com.example.nurdor_volunteer_app_v3.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeUtils {

    companion object {
        fun changeDateFormat(inputDate: LocalDateTime, pattern: String): String {
            val outputFormatter = DateTimeFormatter.ofPattern(pattern)
            val outputDate = inputDate.format(outputFormatter)
            return outputDate.toString()
        }

        fun covertToLocalDateTime(inputDateString: String, pattern: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return LocalDateTime.parse(inputDateString, formatter)
        }

        fun calculateDuration(startTime: LocalDateTime, endTime: LocalDateTime): Long {
            return Duration.between(startTime, endTime).seconds / 60
        }
    }
}