package com.example.learncompose.data.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 返回 Flow，Room 会自动在后台线程监听数据库变化
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // 👈 直接返回 Cursor，方便 ContentProvider 包装
    @Query("SELECT * FROM users")
    fun selectAllUsersCursor(): Cursor

    @Query("SELECT * FROM users WHERE id = :id")
    fun selectUserByIdCursor(id: Long): Cursor
}