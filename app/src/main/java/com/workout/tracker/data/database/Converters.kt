package com.workout.tracker.data.database

import androidx.room.TypeConverter
import com.workout.tracker.data.model.ExerciseCategory
import com.workout.tracker.data.model.MuscleGroup
import com.workout.tracker.data.model.PhotoAngle

class Converters {
    @TypeConverter fun fromMuscleGroup(v: MuscleGroup) = v.name
    @TypeConverter fun toMuscleGroup(v: String) = MuscleGroup.valueOf(v)
    @TypeConverter fun fromCategory(v: ExerciseCategory) = v.name
    @TypeConverter fun toCategory(v: String) = ExerciseCategory.valueOf(v)
    @TypeConverter fun fromAngle(v: PhotoAngle) = v.name
    @TypeConverter fun toAngle(v: String) = PhotoAngle.valueOf(v)
}
