package com.example.learncompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.learncompose.core.model.RoutineCard
import java.time.Instant
import java.time.LocalDate
import kotlin.String

@Entity(tableName = "RoutineCardEntity")
data class RoutineCardEntity(
    val userId: String,
    val cardId: String,
    @PrimaryKey
    val cardText: String,
    val cardColor: Long,
    val isCompleted: Boolean,
    val recordDate: LocalDate,
    val completedAt: Instant
)

fun RoutineCardEntity.asExternalModel() = RoutineCard(
    id = cardId,
    text = cardText,
    color = cardColor,
    isCompleted = isCompleted,
    recordDate = recordDate,
    completedAt = completedAt
)

fun RoutineCard.asEntity(userId: String) = RoutineCardEntity(
    userId = userId,
    cardId = id,
    cardText = text,
    cardColor = color,
    isCompleted = isCompleted,
    recordDate = recordDate,
    completedAt = completedAt
)