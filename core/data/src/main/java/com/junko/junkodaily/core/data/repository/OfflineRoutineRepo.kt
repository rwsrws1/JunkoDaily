package com.junko.junkodaily.core.data.repository

import com.junko.junkodaily.core.data.api.RoutineRepoApi
import com.junko.junkodaily.core.database.UserDataDao
import com.junko.junkodaily.core.database.entity.RoutineCardEntity
import com.junko.junkodaily.core.database.entity.RoutineDailyLogEntity
import com.junko.junkodaily.core.database.entity.asRoutineCard
import com.junko.junkodaily.core.database.entity.asRoutineCardEntity
import com.junko.junkodaily.core.database.entity.asRoutineCardsAndLogs
import com.junko.junkodaily.core.database.entity.asRoutineDailyLog
import com.junko.junkodaily.core.database.entity.asRoutineDailyLogEntity
import com.junko.junkodaily.core.model.RoutineCard
import com.junko.junkodaily.core.model.RoutineCardWithLog
import com.junko.junkodaily.core.model.RoutineCardsAndLogs
import com.junko.junkodaily.core.model.RoutineDailyLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRoutineRepo @Inject constructor(
    private val userDataDao: UserDataDao
) : RoutineRepoApi {
    override suspend fun insertCard(card: RoutineCard): Long {
        return userDataDao.insertCard(card.asRoutineCardEntity())
    }

    override fun getAllCards(): Flow<List<RoutineCard>> {
        return userDataDao.getAllCards().map { list -> list.map { it.asRoutineCard() } }
    }

    override suspend fun deleteCardById(cardId: Long) {
        userDataDao.deleteCardById(cardId)
    }

    override suspend fun upsertDailyLog(log: RoutineDailyLog) {
        userDataDao.upsertDailyLog(log.asRoutineDailyLogEntity())
    }

    override suspend fun updateDailyLog(log: RoutineDailyLog) {
        userDataDao.updateDailyLog(log.asRoutineDailyLogEntity())
    }

    override fun getCardsWithLogsByDate(date: LocalDate): Flow<List<RoutineCardWithLog>> {
        return userDataDao.getCardsWithLogsByDate(date)
    }

    override fun getCardsAllDailyLog(cardId: Long): Flow<List<RoutineDailyLog>> {
        return userDataDao.getCardsAllDailyLog(cardId).map { list -> list.map { it.asRoutineDailyLog() } }
    }

    override suspend fun getDailyLog(
        cardId: Long,
        date: LocalDate
    ): RoutineDailyLog? {
        return userDataDao.getDailyLog(cardId, date)?.asRoutineDailyLog()
    }

    override suspend fun deleteDailyLog(cardId: Long, date: LocalDate) {
        userDataDao.deleteDailyLog(cardId, date)
    }

    override fun getCardsWithLogs(): Flow<List<RoutineCardsAndLogs>> {
        return userDataDao.getCardsWithLogs().map { list ->
            list.map { it.asRoutineCardsAndLogs() }
        }
    }
}