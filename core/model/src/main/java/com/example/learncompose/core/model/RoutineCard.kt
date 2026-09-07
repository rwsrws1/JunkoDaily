package com.example.learncompose.core.model

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class RoutineCard(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val color: Long = 0xFF9FEFFF,
    val isCompleted: Boolean = false,
    val recordDate: LocalDate = LocalDate.now(),
    val completedAt: Instant = Instant.now()
)