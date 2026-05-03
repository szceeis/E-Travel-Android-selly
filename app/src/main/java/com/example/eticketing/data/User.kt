package com.example.eticketing.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val email: String,
    val password: String,
    val role: String = "user"
)