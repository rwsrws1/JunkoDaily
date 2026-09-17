package com.example.learncompose.core.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

class DateConverters {
    // 处理 LocalDate（仅日期："2026-09-07" <-> LocalDate）
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    // 处理 Instant（精确时刻：时间戳 Long <-> Instant）
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(timeMillis: Long?): Instant? {
        return timeMillis?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromColor(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(colorInt: Int): Color {
        return Color(colorInt)
    }
}