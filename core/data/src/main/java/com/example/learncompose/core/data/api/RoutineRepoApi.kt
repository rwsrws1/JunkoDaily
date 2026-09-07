package com.example.learncompose.core.data.api

import com.example.learncompose.core.model.RoutineCard
import kotlinx.coroutines.flow.Flow

interface RoutineRepoApi {
    fun getRoutineCard(userId: String = "9527") : Flow<List<RoutineCard>>
    suspend fun insertRoutineCard(userId: String = "9527", routineCard: RoutineCard)
    suspend fun deleteRoutineCard(userId: String = "9527", routineCard: RoutineCard)
    suspend fun updateRoutineCard(userId: String = "9527", routineCard: RoutineCard)
}