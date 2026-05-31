package com.workout.tracker.ui.workout.routine

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.Exercise
import com.workout.tracker.data.model.RoutineExercise
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditRoutineViewModel(
    private val repo: WorkoutRepository,
    private val routineId: Long
) : ViewModel() {

    private val _routineExercises = MutableStateFlow<List<RoutineExercise>>(emptyList())
    val routineExercises: StateFlow<List<RoutineExercise>> = _routineExercises

    init {
        if (routineId >= 0) {
            viewModelScope.launch {
                _routineExercises.value = repo.getRoutineExercisesOnce(routineId)
            }
        }
    }

    fun addExercise(exercise: Exercise) {
        val current = _routineExercises.value.toMutableList()
        if (current.none { it.exerciseId == exercise.id }) {
            current.add(
                RoutineExercise(
                    routineId = routineId,
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    orderIndex = current.size
                )
            )
            _routineExercises.value = current
        }
    }

    fun removeExercise(re: RoutineExercise) {
        _routineExercises.value = _routineExercises.value.filter { it.exerciseId != re.exerciseId }
    }

    fun updateTargetSets(re: RoutineExercise, sets: Int) {
        _routineExercises.value = _routineExercises.value.map {
            if (it.exerciseId == re.exerciseId) it.copy(targetSets = sets) else it
        }
    }

    fun updateTargetReps(re: RoutineExercise, reps: Int) {
        _routineExercises.value = _routineExercises.value.map {
            if (it.exerciseId == re.exerciseId) it.copy(targetReps = reps) else it
        }
    }

    fun save(name: String) {
        viewModelScope.launch {
            val id = if (routineId >= 0) {
                repo.updateRoutine(com.workout.tracker.data.model.Routine(id = routineId, name = name))
                routineId
            } else {
                repo.createRoutine(name)
            }
            repo.setRoutineExercises(id, _routineExercises.value)
        }
    }

    class Factory(private val app: WorkoutApp, private val routineId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditRoutineViewModel(app.workoutRepository, routineId) as T
        }
    }
}
