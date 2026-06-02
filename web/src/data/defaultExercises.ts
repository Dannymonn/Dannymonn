import type { Exercise } from '../types'

export const DEFAULT_EXERCISES: Omit<Exercise, 'id'>[] = [
  // Chest
  { name: 'Bench Press', muscleGroup: 'CHEST', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Incline Bench Press', muscleGroup: 'CHEST', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Dumbbell Fly', muscleGroup: 'CHEST', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Dumbbell Press', muscleGroup: 'CHEST', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Cable Fly', muscleGroup: 'CHEST', category: 'CABLE', usesWeight: true, isCustom: false },
  { name: 'Push-Up', muscleGroup: 'CHEST', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  // Back
  { name: 'Deadlift', muscleGroup: 'BACK', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Barbell Row', muscleGroup: 'BACK', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Pull-Up', muscleGroup: 'BACK', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Lat Pulldown', muscleGroup: 'BACK', category: 'MACHINE', usesWeight: true, isCustom: false },
  { name: 'Seated Cable Row', muscleGroup: 'BACK', category: 'CABLE', usesWeight: true, isCustom: false },
  { name: 'Dumbbell Row', muscleGroup: 'BACK', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  // Shoulders
  { name: 'Overhead Press', muscleGroup: 'SHOULDERS', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Dumbbell Shoulder Press', muscleGroup: 'SHOULDERS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Lateral Raise', muscleGroup: 'SHOULDERS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Face Pull', muscleGroup: 'SHOULDERS', category: 'CABLE', usesWeight: true, isCustom: false },
  // Biceps
  { name: 'Barbell Curl', muscleGroup: 'BICEPS', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Dumbbell Curl', muscleGroup: 'BICEPS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Hammer Curl', muscleGroup: 'BICEPS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Cable Curl', muscleGroup: 'BICEPS', category: 'CABLE', usesWeight: true, isCustom: false },
  // Triceps
  { name: 'Tricep Pushdown', muscleGroup: 'TRICEPS', category: 'CABLE', usesWeight: true, isCustom: false },
  { name: 'Skull Crusher', muscleGroup: 'TRICEPS', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Overhead Tricep Extension', muscleGroup: 'TRICEPS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Tricep Dip', muscleGroup: 'TRICEPS', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Close-Grip Bench Press', muscleGroup: 'TRICEPS', category: 'BARBELL', usesWeight: true, isCustom: false },
  // Legs
  { name: 'Squat', muscleGroup: 'LEGS', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Leg Press', muscleGroup: 'LEGS', category: 'MACHINE', usesWeight: true, isCustom: false },
  { name: 'Romanian Deadlift', muscleGroup: 'LEGS', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Leg Curl', muscleGroup: 'LEGS', category: 'MACHINE', usesWeight: true, isCustom: false },
  { name: 'Leg Extension', muscleGroup: 'LEGS', category: 'MACHINE', usesWeight: true, isCustom: false },
  { name: 'Walking Lunge', muscleGroup: 'LEGS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Bulgarian Split Squat', muscleGroup: 'LEGS', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  { name: 'Calf Raise', muscleGroup: 'LEGS', category: 'MACHINE', usesWeight: true, isCustom: false },
  // Glutes
  { name: 'Hip Thrust', muscleGroup: 'GLUTES', category: 'BARBELL', usesWeight: true, isCustom: false },
  { name: 'Glute Bridge', muscleGroup: 'GLUTES', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Cable Kickback', muscleGroup: 'GLUTES', category: 'CABLE', usesWeight: true, isCustom: false },
  // Core
  { name: 'Plank', muscleGroup: 'CORE', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Crunch', muscleGroup: 'CORE', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Hanging Leg Raise', muscleGroup: 'CORE', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
  { name: 'Cable Crunch', muscleGroup: 'CORE', category: 'CABLE', usesWeight: true, isCustom: false },
  { name: 'Russian Twist', muscleGroup: 'CORE', category: 'DUMBBELL', usesWeight: true, isCustom: false },
  // Cardio
  { name: 'Treadmill', muscleGroup: 'CARDIO', category: 'CARDIO', usesWeight: false, isCustom: false },
  { name: 'Rowing Machine', muscleGroup: 'CARDIO', category: 'CARDIO', usesWeight: false, isCustom: false },
  { name: 'Stationary Bike', muscleGroup: 'CARDIO', category: 'CARDIO', usesWeight: false, isCustom: false },
  { name: 'Jump Rope', muscleGroup: 'CARDIO', category: 'BODYWEIGHT', usesWeight: false, isCustom: false },
]
