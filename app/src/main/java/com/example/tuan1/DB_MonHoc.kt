package com.example.tuan1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "monhoc",
    foreignKeys = [
        ForeignKey(
            entity = HocKy::class,
            parentColumns = ["maHocKy"],
            childColumns = ["maHocKy"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DB_MonHoc(
    @PrimaryKey
    val maMon: String,
    val tenMon: String,
    val tinChi: Int,
    val maHocKy: Int
)