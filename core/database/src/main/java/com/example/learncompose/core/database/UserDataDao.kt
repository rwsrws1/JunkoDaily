package com.example.learncompose.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.learncompose.core.database.entity.PopulatedRoutineCardEntity
import com.example.learncompose.core.database.entity.RoutineCardEntity
import com.example.learncompose.core.database.entity.RoutineDailyLogEntity
import com.example.learncompose.core.model.RoutineCardWithLog
import com.example.learncompose.core.model.RoutineDailyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface UserDataDao {
    // ================= 1. 静态卡牌增删改查 =================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: RoutineCardEntity): Long

    @Query("SELECT * FROM routine_card")
    fun getAllCards(): Flow<List<RoutineCardEntity>>

    @Query("DELETE FROM routine_card WHERE id = :cardId")
    suspend fun deleteCardById(cardId: Long)


    // ================= 2. 每日打卡记录 CRUD =================

    /**
     * 【增/改 - UPSERT】
     * 存在则更新，不存在则插入。
     * 因为 (cardId, recordDate) 是联合主键，REPLACE 策略可以自动处理覆盖更新。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLog(log: RoutineDailyLogEntity)

    /**
     * 【改】单独修改某天卡牌的属性
     */
    @Update
    suspend fun updateDailyLog(log: RoutineDailyLogEntity)

    /**
     * 【查】获取指定日期（如 "2026-03-30"）的所有卡牌及其打卡状态
     * 使用 LEFT JOIN：即使当天还没有插入 log 记录，卡牌也会被查出来（其 log 状态字段置为默认值）
     */
    @Query("""
        SELECT 
            c.id AS cardId,
            c.cardText AS cardText,
            c.cardColor AS cardColor,
            l.recordDate AS recordDate,
            COALESCE(l.isCompleted, 0) AS isCompleted,
            l.completedAt AS completedAt
        FROM routine_card c
        LEFT JOIN routine_daily_log l 
            ON c.id = l.cardId AND l.recordDate = :date
    """)
    fun getCardsWithLogsByDate(date: LocalDate): Flow<List<RoutineCardWithLog>>

    @Query("SELECT * FROM routine_daily_log WHERE cardId = :cardId")
    fun getCardsAllDailyLog(cardId: Long): Flow<List<RoutineDailyLogEntity>>

    /**
     * 【查】获取某张卡牌在特定日期的记录
     */
    @Query("SELECT * FROM routine_daily_log WHERE cardId = :cardId AND recordDate = :date")
    suspend fun getDailyLog(cardId: Long, date: LocalDate): RoutineDailyLogEntity?

    /**
     * 【删】重置/删除某张卡牌在特定日期的打卡记录
     */
    @Query("DELETE FROM routine_daily_log WHERE cardId = :cardId AND recordDate = :date")
    suspend fun deleteDailyLog(cardId: Long, date: LocalDate)

    @Transaction
    @Query("SELECT * FROM routine_card")
    fun getCardsWithLogs(): Flow<List<PopulatedRoutineCardEntity>>
}