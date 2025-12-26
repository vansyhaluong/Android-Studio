package com.example.tuan1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TKBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(tkb: ThoiKhoaBieu)
     @Update
    fun update(tkb: ThoiKhoaBieu)
    @Query("DELETE FROM thoikhoabieu WHERE maTKB = :id")
    suspend fun deleteById(id: Int)

    // Lấy tất cả TKB
    @Query("SELECT * FROM thoikhoabieu ")
     fun getAll(): List<ThoiKhoaBieu>

    // Lấy TKB theo học kỳ và tuần
    @Query("""
        SELECT thoikhoabieu.*, monhoc.tenMon 
        FROM thoikhoabieu 
        INNER JOIN monhoc ON thoikhoabieu.maMon = monhoc.maMon
        WHERE tuan = :tuan
       
    """)
    suspend fun getByTuan( tuan: Int): List<TKBTenMon>

    @Query("SELECT * FROM thoikhoabieu WHERE maMon = :maMon")
    suspend fun getByMon(maMon: Int): List<ThoiKhoaBieu>

    @Query("SELECT DISTINCT tuan FROM thoikhoabieu ORDER BY tuan")
    suspend fun getAllTuan(): List<Int>
}