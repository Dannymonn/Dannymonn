package com.workout.tracker

import android.app.Application
import com.workout.tracker.data.database.AppDatabase
import com.workout.tracker.data.repository.GoalRepository
import com.workout.tracker.data.repository.RunRepository
import com.workout.tracker.data.repository.WorkoutRepository

class WorkoutApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val workoutRepository by lazy {
        WorkoutRepository(
            database.workoutSessionDao(),
            database.workoutSetDao(),
            database.exerciseDao(),
            database.routineDao()
        )
    }
    val runRepository by lazy { RunRepository(database.runSessionDao()) }
    val goalRepository by lazy { GoalRepository(database.goalDao()) }
}
