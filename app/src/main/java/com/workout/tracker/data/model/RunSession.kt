package com.workout.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_sessions")
data class RunSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,
    val distanceKm: Float = 0f,
    val avgHeartRate: Int = 0,
    val maxHeartRate: Int = 0,
    val avgCadence: Int = 0,
    val caloriesBurned: Int = 0,
    val notes: String = ""
) {
    val paceMinPerKm: Float
        get() = if (distanceKm > 0) (durationSeconds / 60f) / distanceKm else 0f
}
