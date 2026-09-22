package com.tivora.aquahogar.core.time

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

object DateUtils {
    private val zone: ZoneId get() = ZoneId.systemDefault()
    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CO"))
    private val shortFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd MMM", Locale("es", "CO"))

    fun toLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

    fun startOfDay(date: LocalDate): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun endOfDay(date: LocalDate): Long =
        date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

    fun format(millis: Long): String = toLocalDate(millis).format(dateFormatter)

    fun formatShort(millis: Long): String = toLocalDate(millis).format(shortFormatter)

    fun today(): LocalDate = LocalDate.now(zone)

    fun currentYearMonth(): YearMonth = YearMonth.now(zone)

    fun weekKey(date: LocalDate): String {
        val week = date.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = date.get(WeekFields.ISO.weekBasedYear())
        return "$year-W${week.toString().padStart(2, '0')}"
    }

    fun monthKey(date: LocalDate): String = YearMonth.from(date).toString()
}
