package com.example.learncompose.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.learncompose.core.model.RoutineDailyLog
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "routine_daily_log",
    primaryKeys = ["cardId", "recordDate"],
    indices = [Index(value = ["recordDate"])], // 针对按日期查询进行索引优化
    foreignKeys = [
        ForeignKey(
            entity = RoutineCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE // 卡牌被删时，自动清理历史记录
        )
    ]
)
data class RoutineDailyLogEntity(
    val cardId: Long,
    val recordDate: LocalDate,
    val isCompleted: Boolean,
    val completedAt: Instant,
)

fun RoutineDailyLogEntity.asRoutineDailyLog() = RoutineDailyLog(
    cardId = cardId,
    recordDate = recordDate,
    isCompleted = isCompleted,
    completedAt = completedAt,
)

fun RoutineDailyLog.asRoutineDailyLogEntity() = RoutineDailyLogEntity(
    cardId = cardId,
    recordDate = recordDate ?: LocalDate.now(),
    isCompleted = isCompleted,
    completedAt = completedAt ?: Instant.now(),
)