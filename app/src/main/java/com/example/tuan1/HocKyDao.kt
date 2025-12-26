package com.example.tuan1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HocKyDao {
    @Query("SELECT * FROM hocky")
    fun getAll(): List<HocKy>
    @Query("SELECT COUNT(*) FROM hocky")
    fun count(): Int
    @Insert
    suspend fun insert(hocKy: HocKy)
}