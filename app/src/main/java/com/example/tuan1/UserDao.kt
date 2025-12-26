package com.example.tuan1

import androidx.room.Dao
import com.example.tuan1.User
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE maSV = :maSV and password=:password limit 1")
    suspend fun login(maSV: String, password: String): User?
    @Query("SELECT * FROM users WHERE maSV = :maSV LIMIT 1")
    suspend fun getUserByMaSV(maSV: String): User?
}