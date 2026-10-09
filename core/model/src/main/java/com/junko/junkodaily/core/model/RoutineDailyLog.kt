package com.junko.junkodaily.core.model

import java.time.Instant
import java.time.LocalDate

data class RoutineDailyLog(
    val cardId: Long = 0,
    val recordDate: LocalDate? = LocalDate.now(),
    val isCompleted: Boolean = false,
    val completedAt: Instant? = Instant.now(),
)
