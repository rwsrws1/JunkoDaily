package com.example.learncompose.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.learncompose.core.database.entity.RoutineCardEntity
import com.example.learncompose.core.model.RoutineCard
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

@Dao
interface UserDataDao {
    @Query("SELECT * FROM RoutineCardEntity WHERE userId = :userId")
    fun getRoutineCard(userId: String) : Flow<List<RoutineCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineCard(routineCard: RoutineCardEntity)

    @Delete
    suspend fun deleteRoutineCard(routineCard: RoutineCardEntity)

    @Update
    suspend fun updateRoutineCard(routineCard: RoutineCardEntity)
}