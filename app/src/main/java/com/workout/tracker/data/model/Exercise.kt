package com.workout.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, LEGS, GLUTES, CORE, FULL_BODY, CARDIO, OTHER
}

enum class ExerciseCategory {
    BARBELL, DUMBBELL, MACHINE, CABLE, BODYWEIGHT, KETTLEBELL, CARDIO, OTHER
}

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: MuscleGroup,
    val category: ExerciseCategory,
    val usesWeight: Boolean = true,
    val isCustom: Boolean = false,
    val notes: String = ""
)
