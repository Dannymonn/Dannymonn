package com.workout.tracker.data.database.dao

import androidx.room.*
import com.workout.tracker.data.model.Routine
import com.workout.tracker.data.model.RoutineExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getById(id: Long): Routine?

    @Insert
    suspend fun insert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex")
    fun getExercisesForRoutine(routineId: Long): Flow<List<RoutineExercise>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex")
    suspend fun getExercisesForRoutineOnce(routineId: Long): List<RoutineExercise>

    @Insert
    suspend fun insertExercise(exercise: RoutineExercise): Long

    @Insert
    suspend fun insertExercises(exercises: List<RoutineExercise>)

    @Update
    suspend fun updateExercise(exercise: RoutineExercise)

    @Delete
    suspend fun deleteExercise(exercise: RoutineExercise)

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteExercisesForRoutine(routineId: Long)
}
