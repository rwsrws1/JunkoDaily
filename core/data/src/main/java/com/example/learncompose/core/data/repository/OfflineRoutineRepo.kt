package com.example.learncompose.core.data.repository

import com.example.learncompose.core.data.api.RoutineRepoApi
import com.example.learncompose.core.database.UserDataDao
import com.example.learncompose.core.database.entity.asEntity
import com.example.learncompose.core.database.entity.asExternalModel
import com.example.learncompose.core.model.RoutineCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRoutineRepo @Inject constructor(
    private val userDataDao: UserDataDao
) : RoutineRepoApi {
    override fun getRoutineCard(userId: String): Flow<List<RoutineCard>> {
        return userDataDao.getRoutineCard(userId).map { cardEntities -> cardEntities.map { it.asExternalModel() } }
    }

    override suspend fun insertRoutineCard(userId: String, routineCard: RoutineCard) {
        userDataDao.insertRoutineCard(routineCard.asEntity(userId))
    }

    override suspend fun deleteRoutineCard(userId: String, routineCard: RoutineCard) {
        userDataDao.deleteRoutineCard(routineCard.asEntity(userId))
    }

    override suspend fun updateRoutineCard(userId: String, routineCard: RoutineCard) {
        userDataDao.updateRoutineCard(routineCard.asEntity(userId))
    }
}