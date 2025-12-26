package com.example.tuan1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val maSV: String,
    val password: String,
    val role: String
)