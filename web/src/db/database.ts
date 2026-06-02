import Dexie, { Table } from 'dexie'
import type { Exercise, WorkoutSession, WorkoutSet, Routine, RoutineExercise, RunSession, Goal, WeightEntry, ProgressPhoto } from '../types'
import { DEFAULT_EXERCISES } from '../data/defaultExercises'

class WorkoutDB extends Dexie {
  exercises!: Table<Exercise>
  workoutSessions!: Table<WorkoutSession>
  workoutSets!: Table<WorkoutSet>
  routines!: Table<Routine>
  routineExercises!: Table<RoutineExercise>
  runSessions!: Table<RunSession>
  goals!: Table<Goal>
  weightEntries!: Table<WeightEntry>
  progressPhotos!: Table<ProgressPhoto>

  constructor() {
    super('WorkoutTrackerDB')
    this.version(1).stores({
      exercises: '++id, name, muscleGroup, isCustom',
      workoutSessions: '++id, startTime, routineId',
      workoutSets: '++id, sessionId, exerciseId, completedAt',
      routines: '++id, createdAt',
      routineExercises: '++id, routineId, orderIndex',
      runSessions: '++id, startTime',
      goals: '++id, createdAt',
      weightEntries: '++id, recordedAt',
      progressPhotos: '++id, takenAt'
    })
    this.on('populate', async () => {
      await this.exercises.bulkAdd(DEFAULT_EXERCISES)
    })
  }
}

export const db = new WorkoutDB()
