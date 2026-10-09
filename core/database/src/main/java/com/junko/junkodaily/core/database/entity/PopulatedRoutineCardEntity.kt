package com.junko.junkodaily.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.junko.junkodaily.core.model.RoutineCardsAndLogs

data class PopulatedRoutineCardEntity(
    @Embedded
    val card: RoutineCardEntity,

    @Relation(
        parentColumn = "id",      // RoutineCardEntity 的主键
        entityColumn = "cardId"   // RoutineDailyLogEntity 的外键
    )
    val logs: List<RoutineDailyLogEntity>
)

// 转换函数：将 Database 实体映射为领域模型 (Domain Model)
fun PopulatedRoutineCardEntity.asRoutineCardsAndLogs() = RoutineCardsAndLogs(
    card = card.asRoutineCard(),
    logs = logs.map { it.asRoutineDailyLog() }
)