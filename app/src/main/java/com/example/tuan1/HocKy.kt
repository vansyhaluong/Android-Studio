package com.example.tuan1

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName ="hocky")
    data class HocKy(
        @PrimaryKey(autoGenerate = true)
        val maHocKy: Int = 0,
        val tenHocKy: String
    )
