package com.example.learncompose.core.model

import java.time.Instant
import java.time.LocalDate

data class RoutineCardWithLog(
    val cardId: Long,
    val cardText: String,
    val cardColor: Long,
    val recordDate: LocalDate?,
    val isCompleted: Boolean,
    val completedAt: Instant?,
)
