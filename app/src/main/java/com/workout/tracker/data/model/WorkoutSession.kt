package com.workout.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long? = null,
    val routineName: String = "Free Workout",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val notes: String = "",
    val totalVolume: Float = 0f
)
