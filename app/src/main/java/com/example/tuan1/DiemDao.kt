package com.example.tuan1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tuan1.Diem
@Dao
interface DiemDao {

    // 1. Lấy tất cả điểm
    @Query("SELECT * FROM diem")
    fun getAll(): List<Diem>

    // 2. Lấy điểm theo môn và học kỳ
    @Query("SELECT * FROM diem WHERE maMon = :maMon")
    fun getDiemByMonHoc(maMon: String): Diem?

    // 3. Lấy tất cả điểm của 1 môn
    @Query("SELECT * FROM diem WHERE maMon = :maMon")
    fun getAllDiemByMonHoc(maMon: String): List<Diem>

    // 4. Thêm điểm mới
    @Insert
    fun insert(diem: Diem)

    // 5. Cập nhật điểm
    @Update
    fun update(diem: Diem)

    // 6. Xoá điểm
    @Delete
    fun delete(diem: Diem)

    // 7. Lấy điểm tổng kết (nếu muốn tính trực tiếp ở query)
    @Query("SELECT diemGK*0.4 + diemCK*0.6 AS diemTongKet FROM diem WHERE maMon = :maMon")
    fun getDiemTongKet(maMon: String): Float?
}