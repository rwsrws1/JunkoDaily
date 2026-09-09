package com.example.learncompose.core.data.api

import com.example.learncompose.core.database.entity.RoutineCardEntity
import com.example.learncompose.core.database.entity.RoutineDailyLogEntity
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface RoutineRepoApi {
    suspend fun insertCard(card: RoutineCard): Long
    fun getAllCards(): Flow<List<RoutineCard>>
    suspend fun deleteCardById(cardId: Long)
    suspend fun upsertDailyLog(log: RoutineDailyLog)
    suspend fun updateDailyLog(log: RoutineDailyLog)
    fun getCardsWithLogsByDate(date: LocalDate): Flow<List<RoutineCardWithLog>>
    fun getCardsAllDailyLog(cardId: Long): Flow<List<RoutineDailyLog>>
    suspend fun getDailyLog(cardId: Long, date: LocalDate): RoutineDailyLog?
    suspend fun deleteDailyLog(cardId: Long, date: LocalDate)
}