package com.junko.junkodaily.core.model

import java.time.Instant
import java.time.LocalDate

data class RoutineCardWithLog(
    val cardId: Long = 0,
    val cardText: String = "",
    val cardColor: Long = 0,
    val cardImage: String = "",
    val cardShape: String = "",
    val recordDate: LocalDate? = LocalDate.now(),
    val isCompleted: Boolean = false,
    val completedAt: Instant? = Instant.now(),
)
