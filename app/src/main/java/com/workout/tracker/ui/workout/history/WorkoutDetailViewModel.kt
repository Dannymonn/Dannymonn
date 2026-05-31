package com.workout.tracker.ui.workout.history

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WorkoutDetailViewModel(
    private val repo: WorkoutRepository,
    private val sessionId: Long
) : ViewModel() {

    val session = flow {
        emit(repo.allSessions.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value.find { it.id == sessionId })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sets = repo.getSetsForSession(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val app: WorkoutApp, private val sessionId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WorkoutDetailViewModel(app.workoutRepository, sessionId) as T
        }
    }
}
