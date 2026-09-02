package com.example.learncompose.feature.experiment.data.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) = userDao.insertUser(user)

    suspend fun delete(user: User) = userDao.deleteUser(user)
}