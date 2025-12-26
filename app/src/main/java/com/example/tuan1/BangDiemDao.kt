package com.example.tuan1

import androidx.room.Dao
import androidx.room.Query
@Dao
interface BangDiemDao{
    @Query("""
        SELECT 
            mh.maHocKy AS hocKy,
            mh.tenMon AS tenMon,
            bd.diemGK AS diemGK,
            bd.diemCK AS diemCK,
            (bd.diemGK * 0.4 + bd.diemCK * 0.6) AS diemTK
        FROM diem bd
        JOIN monhoc mh ON bd.maMon = mh.maMon
        ORDER BY mh.mahocKy
    """)
    suspend fun bangDiemTatCaHocKy(): List<BangDiemThongKe>
}