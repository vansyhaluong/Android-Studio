package com.example.tuan1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diem",
    foreignKeys = [
        ForeignKey(
            entity = DB_MonHoc::class,
            parentColumns = ["maMon"],
            childColumns = ["maMon"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Diem(
    @PrimaryKey(autoGenerate = true)
    val maDiem: Int = 0,
    val maMon: String,
    val diemGK: Float,
    val diemCK: Float
)