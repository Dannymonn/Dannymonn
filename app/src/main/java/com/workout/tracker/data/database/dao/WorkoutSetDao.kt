package com.workout.tracker.data.database.dao

import androidx.room.*
import com.workout.tracker.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY exerciseName, setNumber")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY exerciseName, setNumber")
    suspend fun getSetsForSessionOnce(sessionId: Long): List<WorkoutSet>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY completedAt DESC LIMIT 20")
    fun getHistoryForExercise(exerciseId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT MAX(weightKg) FROM workout_sets WHERE exerciseId = :exerciseId")
    fun getPersonalBest(exerciseId: Long): Flow<Float?>

    @Insert
    suspend fun insert(set: WorkoutSet): Long

    @Update
    suspend fun update(set: WorkoutSet)

    @Delete
    suspend fun delete(set: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)
}
