package com.junko.junkodaily.core.data.api

import com.junko.junkodaily.core.database.entity.RoutineCardEntity
import com.junko.junkodaily.core.database.entity.RoutineDailyLogEntity
import com.junko.junkodaily.core.model.RoutineCard
import com.junko.junkodaily.core.model.RoutineCardWithLog
import com.junko.junkodaily.core.model.RoutineCardsAndLogs
import com.junko.junkodaily.core.model.RoutineDailyLog
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
    fun getCardsWithLogs(): Flow<List<RoutineCardsAndLogs>>
}