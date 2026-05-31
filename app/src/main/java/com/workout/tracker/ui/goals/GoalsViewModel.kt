package com.workout.tracker.ui.goals

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.PhotoAngle
import com.workout.tracker.data.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(private val repo: GoalRepository) : ViewModel() {

    val latestGoal = repo.latestGoal.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allWeightEntries = repo.allWeightEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val latestWeight = repo.latestWeight.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allPhotos = repo.allPhotos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setGoal(targetKg: Float, currentKg: Float, targetDate: Long? = null) {
        viewModelScope.launch { repo.setGoal(targetKg, currentKg, targetDate) }
    }

    fun logWeight(weightKg: Float, notes: String = "") {
        viewModelScope.launch { repo.logWeight(weightKg, notes) }
    }

    fun savePhoto(filePath: String, angle: PhotoAngle, weightKg: Float?) {
        viewModelScope.launch { repo.savePhoto(filePath, angle, weightKg) }
    }

    fun deletePhoto(photo: com.workout.tracker.data.model.ProgressPhoto) {
        viewModelScope.launch { repo.deletePhoto(photo) }
    }

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GoalsViewModel(app.goalRepository) as T
        }
    }
}
