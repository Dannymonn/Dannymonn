package com.workout.tracker.data.database

import com.workout.tracker.data.model.Exercise
import com.workout.tracker.data.model.ExerciseCategory.*
import com.workout.tracker.data.model.MuscleGroup.*

object DefaultExercises {
    val list = listOf(
        // Chest
        Exercise(name = "Bench Press", muscleGroup = CHEST, category = BARBELL),
        Exercise(name = "Incline Bench Press", muscleGroup = CHEST, category = BARBELL),
        Exercise(name = "Decline Bench Press", muscleGroup = CHEST, category = BARBELL),
        Exercise(name = "Dumbbell Fly", muscleGroup = CHEST, category = DUMBBELL),
        Exercise(name = "Dumbbell Press", muscleGroup = CHEST, category = DUMBBELL),
        Exercise(name = "Cable Fly", muscleGroup = CHEST, category = CABLE),
        Exercise(name = "Push-Up", muscleGroup = CHEST, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Chest Dip", muscleGroup = CHEST, category = BODYWEIGHT, usesWeight = false),
        // Back
        Exercise(name = "Deadlift", muscleGroup = BACK, category = BARBELL),
        Exercise(name = "Barbell Row", muscleGroup = BACK, category = BARBELL),
        Exercise(name = "Pull-Up", muscleGroup = BACK, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Chin-Up", muscleGroup = BACK, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Lat Pulldown", muscleGroup = BACK, category = MACHINE),
        Exercise(name = "Seated Cable Row", muscleGroup = BACK, category = CABLE),
        Exercise(name = "Dumbbell Row", muscleGroup = BACK, category = DUMBBELL),
        Exercise(name = "T-Bar Row", muscleGroup = BACK, category = BARBELL),
        // Shoulders
        Exercise(name = "Overhead Press", muscleGroup = SHOULDERS, category = BARBELL),
        Exercise(name = "Dumbbell Shoulder Press", muscleGroup = SHOULDERS, category = DUMBBELL),
        Exercise(name = "Lateral Raise", muscleGroup = SHOULDERS, category = DUMBBELL),
        Exercise(name = "Front Raise", muscleGroup = SHOULDERS, category = DUMBBELL),
        Exercise(name = "Rear Delt Fly", muscleGroup = SHOULDERS, category = DUMBBELL),
        Exercise(name = "Face Pull", muscleGroup = SHOULDERS, category = CABLE),
        // Biceps
        Exercise(name = "Barbell Curl", muscleGroup = BICEPS, category = BARBELL),
        Exercise(name = "Dumbbell Curl", muscleGroup = BICEPS, category = DUMBBELL),
        Exercise(name = "Hammer Curl", muscleGroup = BICEPS, category = DUMBBELL),
        Exercise(name = "Preacher Curl", muscleGroup = BICEPS, category = MACHINE),
        Exercise(name = "Cable Curl", muscleGroup = BICEPS, category = CABLE),
        Exercise(name = "Incline Dumbbell Curl", muscleGroup = BICEPS, category = DUMBBELL),
        // Triceps
        Exercise(name = "Tricep Pushdown", muscleGroup = TRICEPS, category = CABLE),
        Exercise(name = "Skull Crusher", muscleGroup = TRICEPS, category = BARBELL),
        Exercise(name = "Overhead Tricep Extension", muscleGroup = TRICEPS, category = DUMBBELL),
        Exercise(name = "Tricep Dip", muscleGroup = TRICEPS, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Close-Grip Bench Press", muscleGroup = TRICEPS, category = BARBELL),
        // Legs
        Exercise(name = "Squat", muscleGroup = LEGS, category = BARBELL),
        Exercise(name = "Front Squat", muscleGroup = LEGS, category = BARBELL),
        Exercise(name = "Leg Press", muscleGroup = LEGS, category = MACHINE),
        Exercise(name = "Hack Squat", muscleGroup = LEGS, category = MACHINE),
        Exercise(name = "Romanian Deadlift", muscleGroup = LEGS, category = BARBELL),
        Exercise(name = "Leg Curl", muscleGroup = LEGS, category = MACHINE),
        Exercise(name = "Leg Extension", muscleGroup = LEGS, category = MACHINE),
        Exercise(name = "Walking Lunge", muscleGroup = LEGS, category = DUMBBELL),
        Exercise(name = "Bulgarian Split Squat", muscleGroup = LEGS, category = DUMBBELL),
        Exercise(name = "Calf Raise", muscleGroup = LEGS, category = MACHINE),
        // Glutes
        Exercise(name = "Hip Thrust", muscleGroup = GLUTES, category = BARBELL),
        Exercise(name = "Glute Bridge", muscleGroup = GLUTES, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Cable Kickback", muscleGroup = GLUTES, category = CABLE),
        // Core
        Exercise(name = "Plank", muscleGroup = CORE, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Crunch", muscleGroup = CORE, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Hanging Leg Raise", muscleGroup = CORE, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Ab Wheel Rollout", muscleGroup = CORE, category = BODYWEIGHT, usesWeight = false),
        Exercise(name = "Cable Crunch", muscleGroup = CORE, category = CABLE),
        Exercise(name = "Russian Twist", muscleGroup = CORE, category = DUMBBELL),
        // Cardio
        Exercise(name = "Treadmill", muscleGroup = CARDIO, category = CARDIO, usesWeight = false),
        Exercise(name = "Rowing Machine", muscleGroup = CARDIO, category = CARDIO, usesWeight = false),
        Exercise(name = "Stationary Bike", muscleGroup = CARDIO, category = CARDIO, usesWeight = false),
        Exercise(name = "Elliptical", muscleGroup = CARDIO, category = CARDIO, usesWeight = false),
        Exercise(name = "Jump Rope", muscleGroup = CARDIO, category = BODYWEIGHT, usesWeight = false)
    )
}
