package com.workout.tracker.data.repository

import com.workout.tracker.data.database.dao.*
import com.workout.tracker.data.model.*
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val sessionDao: WorkoutSessionDao,
    private val setDao: WorkoutSetDao,
    private val exerciseDao: ExerciseDao,
    private val routineDao: RoutineDao
) {
    val allSessions: Flow<List<WorkoutSession>> = sessionDao.getAll()
    val recentSessions: Flow<List<WorkoutSession>> = sessionDao.getRecent()
    val totalWorkouts: Flow<Int> = sessionDao.getTotalCount()
    val totalVolume: Flow<Float?> = sessionDao.getTotalVolume()
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
    val allRoutines: Flow<List<Routine>> = routineDao.getAll()

    fun searchExercises(query: String) = exerciseDao.search(query)
    fun getExercisesByMuscle(group: MuscleGroup) = exerciseDao.getByMuscleGroup(group)
    fun getSetsForSession(id: Long) = setDao.getSetsForSession(id)
    fun getHistoryForExercise(id: Long) = setDao.getHistoryForExercise(id)
    fun getPersonalBest(id: Long) = setDao.getPersonalBest(id)
    fun getRoutineExercises(routineId: Long) = routineDao.getExercisesForRoutine(routineId)

    suspend fun startSession(routineId: Long? = null, routineName: String = "Free Workout"): Long {
        return sessionDao.insert(WorkoutSession(routineId = routineId, routineName = routineName))
    }

    suspend fun finishSession(sessionId: Long) {
        val session = sessionDao.getById(sessionId) ?: return
        val sets = setDao.getSetsForSessionOnce(sessionId)
        val volume = sets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
        sessionDao.update(session.copy(endTime = System.currentTimeMillis(), totalVolume = volume))
    }

    suspend fun logSet(set: WorkoutSet): Long = setDao.insert(set)
    suspend fun updateSet(set: WorkoutSet) = setDao.update(set)
    suspend fun deleteSet(set: WorkoutSet) = setDao.delete(set)

    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insert(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    suspend fun createRoutine(name: String, description: String = ""): Long =
        routineDao.insert(Routine(name = name, description = description))

    suspend fun updateRoutine(routine: Routine) = routineDao.update(routine)
    suspend fun deleteRoutine(routine: Routine) = routineDao.delete(routine)

    suspend fun setRoutineExercises(routineId: Long, exercises: List<RoutineExercise>) {
        routineDao.deleteExercisesForRoutine(routineId)
        routineDao.insertExercises(exercises.mapIndexed { i, e -> e.copy(routineId = routineId, orderIndex = i) })
    }

    suspend fun getRoutineExercisesOnce(routineId: Long) =
        routineDao.getExercisesForRoutineOnce(routineId)
}
