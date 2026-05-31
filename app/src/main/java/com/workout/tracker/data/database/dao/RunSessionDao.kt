package com.workout.tracker.data.database.dao

import androidx.room.*
import com.workout.tracker.data.model.RunSession
import kotlinx.coroutines.flow.Flow

@Dao
interface RunSessionDao {
    @Query("SELECT * FROM run_sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<RunSession>>

    @Query("SELECT * FROM run_sessions ORDER BY startTime DESC LIMIT 5")
    fun getRecent(): Flow<List<RunSession>>

    @Query("SELECT * FROM run_sessions WHERE id = :id")
    suspend fun getById(id: Long): RunSession?

    @Query("SELECT SUM(distanceKm) FROM run_sessions")
    fun getTotalDistance(): Flow<Float?>

    @Query("SELECT MAX(distanceKm) FROM run_sessions")
    fun getLongestRun(): Flow<Float?>

    @Insert
    suspend fun insert(session: RunSession): Long

    @Update
    suspend fun update(session: RunSession)

    @Delete
    suspend fun delete(session: RunSession)
}
