export type MuscleGroup = 'CHEST'|'BACK'|'SHOULDERS'|'BICEPS'|'TRICEPS'|'LEGS'|'GLUTES'|'CORE'|'CARDIO'|'OTHER'
export type ExerciseCategory = 'BARBELL'|'DUMBBELL'|'MACHINE'|'CABLE'|'BODYWEIGHT'|'CARDIO'|'OTHER'
export type PhotoAngle = 'FRONT'|'SIDE'|'BACK'

export interface Exercise {
  id?: number
  name: string
  muscleGroup: MuscleGroup
  category: ExerciseCategory
  usesWeight: boolean
  isCustom: boolean
}

export interface WorkoutSession {
  id?: number
  routineId?: number
  routineName: string
  startTime: number
  endTime?: number
  totalVolume: number
  notes: string
}

export interface WorkoutSet {
  id?: number
  sessionId: number
  exerciseId: number
  exerciseName: string
  setNumber: number
  reps: number
  weightKg: number
  restSeconds: number
  completedAt: number
}

export interface Routine {
  id?: number
  name: string
  description: string
  createdAt: number
}

export interface RoutineExercise {
  id?: number
  routineId: number
  exerciseId: number
  exerciseName: string
  targetSets: number
  targetReps: number
  orderIndex: number
}

export interface RunSession {
  id?: number
  startTime: number
  durationSeconds: number
  distanceKm: number
  avgHeartRate: number
  maxHeartRate: number
  notes: string
}

export interface Goal {
  id?: number
  targetWeightKg: number
  currentWeightKg: number
  createdAt: number
}

export interface WeightEntry {
  id?: number
  weightKg: number
  recordedAt: number
  notes: string
}

export interface ProgressPhoto {
  id?: number
  dataUrl: string
  angle: PhotoAngle
  takenAt: number
  weightAtTime?: number
}
