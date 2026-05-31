package com.workout.tracker.data.database.dao

import androidx.room.*
import com.workout.tracker.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getById(id: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC LIMIT 5")
    fun getRecent(): Flow<List<WorkoutSession>>

    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("SELECT COUNT(*) FROM workout_sessions")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT SUM(totalVolume) FROM workout_sessions")
    fun getTotalVolume(): Flow<Float?>
}
