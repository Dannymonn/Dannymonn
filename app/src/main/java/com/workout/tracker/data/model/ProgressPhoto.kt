package com.workout.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PhotoAngle { FRONT, SIDE, BACK }

@Entity(tableName = "progress_photos")
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val angle: PhotoAngle = PhotoAngle.FRONT,
    val takenAt: Long = System.currentTimeMillis(),
    val weightAtTime: Float? = null,
    val notes: String = ""
)
