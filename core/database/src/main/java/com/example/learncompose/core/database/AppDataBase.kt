package com.example.learncompose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.learncompose.core.database.entity.RoutineCardEntity
import com.example.learncompose.core.database.entity.RoutineDailyLogEntity

// 2. 在 Database 类或 Entity 上注册
@Database(
    entities = [RoutineCardEntity::class, RoutineDailyLogEntity::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
}