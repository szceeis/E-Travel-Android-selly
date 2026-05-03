package com.example.eticketing.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val destinationId: Long,
    val bookingDate: Long,
    val quantity: Int,
    val totalPrice: Double,
    val status: String
)