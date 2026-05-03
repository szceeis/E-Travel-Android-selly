package com.example.eticketing.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val location: String,
    val price: Double,
    val category: String,
    val imageUrl: String? = null,
    val pengelolaId: Long = 0
)