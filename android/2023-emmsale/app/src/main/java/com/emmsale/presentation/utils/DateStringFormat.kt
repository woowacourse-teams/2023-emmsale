package com.emmsale.presentation.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateStringFormat {
    fun getGeneralDateFormat(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateString, formatter)

        val targetDate = LocalDate.of(dateTime.year, dateTime.month, dateTime.dayOfMonth)
        val targetTime = LocalTime.of(dateTime.hour, dateTime.minute)
        val targetDateTime = LocalDateTime.of(targetDate, targetTime)

        val resultFormatter = DateTimeFormatter.ofPattern("yyyy.M.d HH:mm")
        return targetDateTime.format(resultFormatter)
    }
}
