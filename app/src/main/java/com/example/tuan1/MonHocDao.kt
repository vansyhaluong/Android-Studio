package com.example.tuan1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MonHocDao {

    @Insert
    fun insert(mh: DB_MonHoc)
    @Update
    fun update(mh: DB_MonHoc)
    @Delete
    fun delete(mh: DB_MonHoc)
    @Query("SELECT * FROM monhoc")
    fun getAll(): List<DB_MonHoc>
    @Query("SELECT * FROM monhoc WHERE maMon = :maMon")
    fun getSubjectById(maMon:String): DB_MonHoc
    @Query("SELECT * FROM monhoc WHERE maHocKy = :hocKyId")
    fun getByHocKy(hocKyId: Int): List<DB_MonHoc>
    @Query("""
        SELECT * FROM monhoc
        WHERE tenMon LIKE '%' || :keyword || '%'
    """)
    fun searchSubject(keyword: String): List<DB_MonHoc>
}