package com.example.learncompose.core.data.repository

import com.example.learncompose.core.data.api.RoutineRepoApi
import com.example.learncompose.core.database.UserDataDao
import com.example.learncompose.core.database.entity.RoutineCardEntity
import com.example.learncompose.core.database.entity.RoutineDailyLogEntity
import com.example.learncompose.core.database.entity.asRoutineCard
import com.example.learncompose.core.database.entity.asRoutineCardEntity
import com.example.learncompose.core.database.entity.asRoutineDailyLog
import com.example.learncompose.core.database.entity.asRoutineDailyLogEntity
import com.example.learncompose.core.model.RoutineCard
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
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

    override suspend fun getAllCards(): Flow<List<RoutineCard>> {
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

    override suspend fun getDailyLog(
        cardId: Long,
        date: LocalDate
    ): RoutineDailyLog? {
        return userDataDao.getDailyLog(cardId, date)?.asRoutineDailyLog()
    }

    override suspend fun deleteDailyLog(cardId: Long, date: LocalDate) {
        userDataDao.deleteDailyLog(cardId, date)
    }

}