package com.example.learncompose.feature.experiment.data.di

import android.content.Context
import androidx.room.Room
import com.example.learncompose.feature.experiment.data.room.AppDatabase
import com.example.learncompose.feature.experiment.data.room.MIGRATION_3_4
import com.example.learncompose.feature.experiment.data.room.MIGRATION_4_5
import com.example.learncompose.feature.experiment.data.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}