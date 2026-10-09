package com.junko.junkodaily.core.model

data class RoutineCardsAndLogs(
    val card: RoutineCard = RoutineCard(),
    val logs: List<RoutineDailyLog> = listOf(RoutineDailyLog())
)
