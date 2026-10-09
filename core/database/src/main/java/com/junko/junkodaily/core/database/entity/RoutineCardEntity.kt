package com.junko.junkodaily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.junko.junkodaily.core.model.RoutineCard
import kotlin.String

@Entity(tableName = "routine_card")
data class RoutineCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardText: String,
    val cardColor: Long,
    val cardImage: String,
    val cardShape: String
)

fun RoutineCardEntity.asRoutineCard() = RoutineCard(
    id = id,
    cardText = cardText,
    cardColor = cardColor,
    cardImage = cardImage,
    cardShape = cardShape,
)

fun RoutineCard.asRoutineCardEntity() = RoutineCardEntity(
    cardText = cardText,
    cardColor = cardColor,
    cardImage = cardImage,
    cardShape = cardShape,
)