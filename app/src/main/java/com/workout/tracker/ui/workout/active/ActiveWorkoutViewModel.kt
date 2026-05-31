package com.workout.tracker.ui.workout.active

import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.*
import com.workout.tracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActiveExercise(
    val exercise: Exercise,
    val sets: MutableList<WorkoutSet> = mutableListOf()
)

class ActiveWorkoutViewModel(
    private val repo: WorkoutRepository,
    private val routineId: Long,
    private val routineName: String
) : ViewModel() {

    private val _sessionId = MutableStateFlow<Long?>(null)
    val sessionId: StateFlow<Long?> = _sessionId

    private val _activeExercises = MutableStateFlow<List<ActiveExercise>>(emptyList())
    val activeExercises: StateFlow<List<ActiveExercise>> = _activeExercises

    private val _restTimerSeconds = MutableStateFlow<Long?>(null)
    val restTimerSeconds: StateFlow<Long?> = _restTimerSeconds

    val allExercises = repo.allExercises
    val workoutStartTime = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            val id = repo.startSession(
                routineId = if (routineId >= 0) routineId else null,
                routineName = routineName
            )
            _sessionId.value = id
            if (routineId >= 0) loadRoutineExercises(id)
        }
    }

    private suspend fun loadRoutineExercises(sessionId: Long) {
        val routineExs = repo.getRoutineExercisesOnce(routineId)
        val exercises = routineExs.mapNotNull { re ->
            repo.allExercises.first().find { it.id == re.exerciseId }?.let { ex ->
                ActiveExercise(exercise = ex)
            }
        }
        _activeExercises.value = exercises
    }

    fun addExercise(exercise: Exercise) {
        val current = _activeExercises.value.toMutableList()
        if (current.none { it.exercise.id == exercise.id }) {
            current.add(ActiveExercise(exercise = exercise))
            _activeExercises.value = current
        }
    }

    fun removeExercise(exerciseId: Long) {
        _activeExercises.value = _activeExercises.value.filter { it.exercise.id != exerciseId }
    }

    fun logSet(exerciseId: Long, reps: Int, weightKg: Float, restSeconds: Int = 90) {
        val sessionId = _sessionId.value ?: return
        val current = _activeExercises.value.toMutableList()
        val idx = current.indexOfFirst { it.exercise.id == exerciseId }
        if (idx < 0) return
        val ex = current[idx]
        val setNum = ex.sets.size + 1
        val set = WorkoutSet(
            sessionId = sessionId,
            exerciseId = exerciseId,
            exerciseName = ex.exercise.name,
            setNumber = setNum,
            reps = reps,
            weightKg = weightKg,
            isBodyweight = !ex.exercise.usesWeight,
            restSeconds = restSeconds
        )
        viewModelScope.launch {
            val setId = repo.logSet(set)
            ex.sets.add(set.copy(id = setId))
            _activeExercises.value = current.toList()
        }
        _restTimerSeconds.value = restSeconds.toLong()
    }

    fun deleteLastSet(exerciseId: Long) {
        val current = _activeExercises.value.toMutableList()
        val idx = current.indexOfFirst { it.exercise.id == exerciseId }
        if (idx < 0) return
        val ex = current[idx]
        val last = ex.sets.removeLastOrNull() ?: return
        viewModelScope.launch { repo.deleteSet(last) }
        _activeExercises.value = current.toList()
    }

    fun finishWorkout() {
        val sessionId = _sessionId.value ?: return
        viewModelScope.launch { repo.finishSession(sessionId) }
    }

    fun clearRestTimer() { _restTimerSeconds.value = null }

    class Factory(private val app: WorkoutApp, private val routineId: Long, private val routineName: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ActiveWorkoutViewModel(app.workoutRepository, routineId, routineName) as T
        }
    }
}
