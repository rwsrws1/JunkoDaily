package com.example.learncompose.core.model

import java.time.Instant
import java.time.LocalDate

data class RoutineDailyLog(
    val cardId: Long,
    val recordDate: LocalDate?,
    val isCompleted: Boolean,
    val completedAt: Instant?,
)
