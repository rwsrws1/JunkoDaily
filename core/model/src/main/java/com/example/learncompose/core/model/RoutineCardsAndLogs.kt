package com.example.learncompose.core.model

data class RoutineCardsAndLogs(
    val card: RoutineCard = RoutineCard(),
    val logs: List<RoutineDailyLog> = listOf()
)
