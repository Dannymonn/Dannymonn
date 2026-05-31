package com.workout.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetWeightKg: Float,
    val currentWeightKg: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val targetDate: Long? = null,
    val notes: String = ""
)
