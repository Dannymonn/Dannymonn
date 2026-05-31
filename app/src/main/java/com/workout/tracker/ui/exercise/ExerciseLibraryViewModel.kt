package com.workout.tracker.ui.exercise

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.Exercise
import com.workout.tracker.data.model.ExerciseCategory
import com.workout.tracker.data.model.MuscleGroup
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExerciseLibraryViewModel(private val repo: WorkoutRepository) : ViewModel() {

    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises: StateFlow<List<Exercise>> = _query
        .flatMapLatest { q ->
            if (q.isBlank()) repo.allExercises else repo.searchExercises(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun search(q: String) { _query.value = q }

    fun addCustomExercise(name: String, muscleGroup: MuscleGroup, category: ExerciseCategory, usesWeight: Boolean) {
        viewModelScope.launch {
            repo.insertExercise(
                Exercise(name = name, muscleGroup = muscleGroup, category = category, usesWeight = usesWeight, isCustom = true)
            )
        }
    }

    fun deleteExercise(exercise: Exercise) {
        if (exercise.isCustom) viewModelScope.launch { repo.deleteExercise(exercise) }
    }

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ExerciseLibraryViewModel(app.workoutRepository) as T
        }
    }
}
