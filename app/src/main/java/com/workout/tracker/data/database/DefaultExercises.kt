package com.workout.tracker.data.database

import com.workout.tracker.data.model.Exercise
import com.workout.tracker.data.model.ExerciseCategory
import com.workout.tracker.data.model.MuscleGroup

private val BB = ExerciseCategory.BARBELL
private val DB = ExerciseCategory.DUMBBELL
private val MC = ExerciseCategory.MACHINE
private val CB = ExerciseCategory.CABLE
private val BW = ExerciseCategory.BODYWEIGHT
private val CA = ExerciseCategory.CARDIO

private val CHEST     = MuscleGroup.CHEST
private val BACK      = MuscleGroup.BACK
private val SHOULDERS = MuscleGroup.SHOULDERS
private val BICEPS    = MuscleGroup.BICEPS
private val TRICEPS   = MuscleGroup.TRICEPS
private val LEGS      = MuscleGroup.LEGS
private val GLUTES    = MuscleGroup.GLUTES
private val CORE      = MuscleGroup.CORE
private val CARDIO_MG = MuscleGroup.CARDIO

object DefaultExercises {
    val list = listOf(
        // Chest
        Exercise(name = "Bench Press", muscleGroup = CHEST, category = BB),
        Exercise(name = "Incline Bench Press", muscleGroup = CHEST, category = BB),
        Exercise(name = "Decline Bench Press", muscleGroup = CHEST, category = BB),
        Exercise(name = "Dumbbell Fly", muscleGroup = CHEST, category = DB),
        Exercise(name = "Dumbbell Press", muscleGroup = CHEST, category = DB),
        Exercise(name = "Cable Fly", muscleGroup = CHEST, category = CB),
        Exercise(name = "Push-Up", muscleGroup = CHEST, category = BW, usesWeight = false),
        Exercise(name = "Chest Dip", muscleGroup = CHEST, category = BW, usesWeight = false),
        // Back
        Exercise(name = "Deadlift", muscleGroup = BACK, category = BB),
        Exercise(name = "Barbell Row", muscleGroup = BACK, category = BB),
        Exercise(name = "Pull-Up", muscleGroup = BACK, category = BW, usesWeight = false),
        Exercise(name = "Chin-Up", muscleGroup = BACK, category = BW, usesWeight = false),
        Exercise(name = "Lat Pulldown", muscleGroup = BACK, category = MC),
        Exercise(name = "Seated Cable Row", muscleGroup = BACK, category = CB),
        Exercise(name = "Dumbbell Row", muscleGroup = BACK, category = DB),
        Exercise(name = "T-Bar Row", muscleGroup = BACK, category = BB),
        // Shoulders
        Exercise(name = "Overhead Press", muscleGroup = SHOULDERS, category = BB),
        Exercise(name = "Dumbbell Shoulder Press", muscleGroup = SHOULDERS, category = DB),
        Exercise(name = "Lateral Raise", muscleGroup = SHOULDERS, category = DB),
        Exercise(name = "Front Raise", muscleGroup = SHOULDERS, category = DB),
        Exercise(name = "Rear Delt Fly", muscleGroup = SHOULDERS, category = DB),
        Exercise(name = "Face Pull", muscleGroup = SHOULDERS, category = CB),
        // Biceps
        Exercise(name = "Barbell Curl", muscleGroup = BICEPS, category = BB),
        Exercise(name = "Dumbbell Curl", muscleGroup = BICEPS, category = DB),
        Exercise(name = "Hammer Curl", muscleGroup = BICEPS, category = DB),
        Exercise(name = "Preacher Curl", muscleGroup = BICEPS, category = MC),
        Exercise(name = "Cable Curl", muscleGroup = BICEPS, category = CB),
        Exercise(name = "Incline Dumbbell Curl", muscleGroup = BICEPS, category = DB),
        // Triceps
        Exercise(name = "Tricep Pushdown", muscleGroup = TRICEPS, category = CB),
        Exercise(name = "Skull Crusher", muscleGroup = TRICEPS, category = BB),
        Exercise(name = "Overhead Tricep Extension", muscleGroup = TRICEPS, category = DB),
        Exercise(name = "Tricep Dip", muscleGroup = TRICEPS, category = BW, usesWeight = false),
        Exercise(name = "Close-Grip Bench Press", muscleGroup = TRICEPS, category = BB),
        // Legs
        Exercise(name = "Squat", muscleGroup = LEGS, category = BB),
        Exercise(name = "Front Squat", muscleGroup = LEGS, category = BB),
        Exercise(name = "Leg Press", muscleGroup = LEGS, category = MC),
        Exercise(name = "Hack Squat", muscleGroup = LEGS, category = MC),
        Exercise(name = "Romanian Deadlift", muscleGroup = LEGS, category = BB),
        Exercise(name = "Leg Curl", muscleGroup = LEGS, category = MC),
        Exercise(name = "Leg Extension", muscleGroup = LEGS, category = MC),
        Exercise(name = "Walking Lunge", muscleGroup = LEGS, category = DB),
        Exercise(name = "Bulgarian Split Squat", muscleGroup = LEGS, category = DB),
        Exercise(name = "Calf Raise", muscleGroup = LEGS, category = MC),
        // Glutes
        Exercise(name = "Hip Thrust", muscleGroup = GLUTES, category = BB),
        Exercise(name = "Glute Bridge", muscleGroup = GLUTES, category = BW, usesWeight = false),
        Exercise(name = "Cable Kickback", muscleGroup = GLUTES, category = CB),
        // Core
        Exercise(name = "Plank", muscleGroup = CORE, category = BW, usesWeight = false),
        Exercise(name = "Crunch", muscleGroup = CORE, category = BW, usesWeight = false),
        Exercise(name = "Hanging Leg Raise", muscleGroup = CORE, category = BW, usesWeight = false),
        Exercise(name = "Ab Wheel Rollout", muscleGroup = CORE, category = BW, usesWeight = false),
        Exercise(name = "Cable Crunch", muscleGroup = CORE, category = CB),
        Exercise(name = "Russian Twist", muscleGroup = CORE, category = DB),
        // Cardio
        Exercise(name = "Treadmill", muscleGroup = CARDIO_MG, category = CA, usesWeight = false),
        Exercise(name = "Rowing Machine", muscleGroup = CARDIO_MG, category = CA, usesWeight = false),
        Exercise(name = "Stationary Bike", muscleGroup = CARDIO_MG, category = CA, usesWeight = false),
        Exercise(name = "Elliptical", muscleGroup = CARDIO_MG, category = CA, usesWeight = false),
        Exercise(name = "Jump Rope", muscleGroup = CARDIO_MG, category = BW, usesWeight = false)
    )
}
