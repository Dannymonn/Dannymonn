package com.workout.tracker.data.repository

import com.workout.tracker.data.database.dao.GoalDao
import com.workout.tracker.data.model.Goal
import com.workout.tracker.data.model.ProgressPhoto
import com.workout.tracker.data.model.WeightEntry

class GoalRepository(private val dao: GoalDao) {
    val latestGoal = dao.getLatestGoal()
    val allWeightEntries = dao.getAllWeightEntries()
    val latestWeight = dao.getLatestWeight()
    val allPhotos = dao.getAllPhotos()

    suspend fun setGoal(targetWeight: Float, currentWeight: Float, targetDate: Long? = null): Long =
        dao.insertGoal(Goal(targetWeightKg = targetWeight, currentWeightKg = currentWeight, targetDate = targetDate))

    suspend fun updateGoal(goal: Goal) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: Goal) = dao.deleteGoal(goal)

    suspend fun logWeight(weightKg: Float, notes: String = ""): Long =
        dao.insertWeightEntry(WeightEntry(weightKg = weightKg, notes = notes))

    suspend fun deleteWeightEntry(entry: WeightEntry) = dao.deleteWeightEntry(entry)

    suspend fun savePhoto(filePath: String, angle: com.workout.tracker.data.model.PhotoAngle, weightKg: Float?): Long =
        dao.insertPhoto(ProgressPhoto(filePath = filePath, angle = angle, weightAtTime = weightKg))

    suspend fun deletePhoto(photo: ProgressPhoto) = dao.deletePhoto(photo)
}
