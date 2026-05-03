package com.example.eticketing.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(destination: Destination)

    @Update
    suspend fun updateDestination(destination: Destination)

    @Delete
    suspend fun deleteDestination(destination: Destination)

    @Query("SELECT * FROM destinations ORDER BY name ASC")
    fun getAllDestinations(): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE category = :category")
    fun getDestinationsByCategory(category: String): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE id = :id")
    suspend fun getDestinationById(id: Long): Destination?

    @Query("SELECT * FROM destinations WHERE pengelolaId = :pengelolaId ORDER BY name ASC")
    fun getDestinationsByPengelola(pengelolaId: Long): Flow<List<Destination>>
}