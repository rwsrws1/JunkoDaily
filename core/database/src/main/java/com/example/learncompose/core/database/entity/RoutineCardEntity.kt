package com.example.learncompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.learncompose.core.model.RoutineCard
import kotlin.String

@Entity(tableName = "routine_card")
data class RoutineCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardText: String,
    val cardColor: Long,
)

fun RoutineCardEntity.asRoutineCard() = RoutineCard(
    id = id,
    cardText = cardText,
    cardColor = cardColor
)

fun RoutineCard.asRoutineCardEntity() = RoutineCardEntity(
    cardText = cardText,
    cardColor = cardColor
)