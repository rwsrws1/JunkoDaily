package com.example.learncompose.core.model

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class RoutineCard(
    val id: Long = 0,
    val cardText: String = "",
    val cardColor: Long = 0XFFFFFFFF,
    val cardImage: String = "",
    val cardShape: String = ""
)