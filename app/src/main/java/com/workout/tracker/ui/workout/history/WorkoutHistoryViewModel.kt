package com.workout.tracker.ui.workout.history

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class WorkoutHistoryViewModel(private val repo: WorkoutRepository) : ViewModel() {
    val allSessions = repo.allSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WorkoutHistoryViewModel(app.workoutRepository) as T
        }
    }
}
