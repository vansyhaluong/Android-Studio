package com.example.tuan1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "thoikhoabieu",
    foreignKeys = [
        ForeignKey(
            entity = DB_MonHoc::class,
            parentColumns = ["maMon"],
            childColumns = ["maMon"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ThoiKhoaBieu(
    @PrimaryKey(autoGenerate = true)
    val maTKB: Int = 0,
    val maMon: String,
    val tietBD: Int,
    val tietKT: Int,
    val tuan:Int,
    val thu:Int
)