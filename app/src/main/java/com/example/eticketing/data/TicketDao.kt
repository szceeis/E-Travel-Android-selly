package com.example.eticketing.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Insert
    suspend fun bookTicket(ticket: Ticket)

    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY bookingDate DESC")
    fun getTicketsByUserId(userId: Long): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets ORDER BY bookingDate DESC")
    fun getAllTicketsAdmin(): Flow<List<Ticket>>

    @Query("UPDATE tickets SET status = :status WHERE id = :ticketId")
    suspend fun updateStatus(ticketId: Long, status: String)

    @Query("""
        SELECT tickets.* FROM tickets 
        INNER JOIN destinations ON tickets.destinationId = destinations.id 
        WHERE destinations.pengelolaId = :pengelolaId 
        ORDER BY tickets.bookingDate DESC
    """)
    fun getTicketsByPengelola(pengelolaId: Long): Flow<List<Ticket>>
}