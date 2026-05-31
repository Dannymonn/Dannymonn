package com.workout.tracker.ui.dashboard

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.repository.GoalRepository
import com.workout.tracker.data.repository.RunRepository
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val workoutRepo: WorkoutRepository,
    private val runRepo: RunRepository,
    private val goalRepo: GoalRepository
) : ViewModel() {

    val recentSessions = workoutRepo.recentSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentRuns = runRepo.recentRuns.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalWorkouts = workoutRepo.totalWorkouts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalVolume = workoutRepo.totalVolume.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val totalDistance = runRepo.totalDistance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val latestGoal = goalRepo.latestGoal.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val latestWeight = goalRepo.latestWeight.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allRoutines = workoutRepo.allRoutines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(app.workoutRepository, app.runRepository, app.goalRepository) as T
        }
    }
}
