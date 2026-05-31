package com.workout.tracker.ui.workout.routine

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.Routine
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutineListViewModel(private val repo: WorkoutRepository) : ViewModel() {

    val routines = repo.allRoutines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch { repo.deleteRoutine(routine) }
    }

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RoutineListViewModel(app.workoutRepository) as T
        }
    }
}
