package com.workout.tracker.data.repository

import com.workout.tracker.data.database.dao.RunSessionDao
import com.workout.tracker.data.model.RunSession
import kotlinx.coroutines.flow.Flow

class RunRepository(private val dao: RunSessionDao) {
    val allRuns: Flow<List<RunSession>> = dao.getAll()
    val recentRuns: Flow<List<RunSession>> = dao.getRecent()
    val totalDistance: Flow<Float?> = dao.getTotalDistance()
    val longestRun: Flow<Float?> = dao.getLongestRun()

    suspend fun saveRun(run: RunSession): Long = dao.insert(run)
    suspend fun updateRun(run: RunSession) = dao.update(run)
    suspend fun deleteRun(run: RunSession) = dao.delete(run)
}
