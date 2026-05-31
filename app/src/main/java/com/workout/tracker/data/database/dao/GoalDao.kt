package com.workout.tracker.data.database.dao

import androidx.room.*
import com.workout.tracker.data.model.Goal
import com.workout.tracker.data.model.ProgressPhoto
import com.workout.tracker.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC LIMIT 1")
    fun getLatestGoal(): Flow<Goal?>

    @Insert
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM weight_entries ORDER BY recordedAt DESC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightEntry?>

    @Insert
    suspend fun insertWeightEntry(entry: WeightEntry): Long

    @Delete
    suspend fun deleteWeightEntry(entry: WeightEntry)

    @Query("SELECT * FROM progress_photos ORDER BY takenAt DESC")
    fun getAllPhotos(): Flow<List<ProgressPhoto>>

    @Insert
    suspend fun insertPhoto(photo: ProgressPhoto): Long

    @Delete
    suspend fun deletePhoto(photo: ProgressPhoto)
}
