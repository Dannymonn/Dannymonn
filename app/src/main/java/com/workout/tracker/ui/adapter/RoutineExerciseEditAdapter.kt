package com.workout.tracker.ui.adapter

import android.view.*
import android.widget.SeekBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.RoutineExercise
import com.workout.tracker.databinding.ItemRoutineExerciseEditBinding

class RoutineExerciseEditAdapter(
    private val onRemove: (RoutineExercise) -> Unit,
    private val onSetsChanged: (RoutineExercise, Int) -> Unit,
    private val onRepsChanged: (RoutineExercise, Int) -> Unit
) : ListAdapter<RoutineExercise, RoutineExerciseEditAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemRoutineExerciseEditBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRoutineExerciseEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val re = getItem(position)
        holder.binding.tvName.text = re.exerciseName
        holder.binding.etTargetSets.setText(re.targetSets.toString())
        holder.binding.etTargetReps.setText(re.targetReps.toString())
        holder.binding.btnRemove.setOnClickListener { onRemove(re) }
        holder.binding.etTargetSets.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val sets = holder.binding.etTargetSets.text?.toString()?.toIntOrNull() ?: re.targetSets
                onSetsChanged(re, sets)
            }
        }
        holder.binding.etTargetReps.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val reps = holder.binding.etTargetReps.text?.toString()?.toIntOrNull() ?: re.targetReps
                onRepsChanged(re, reps)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RoutineExercise>() {
            override fun areItemsTheSame(a: RoutineExercise, b: RoutineExercise) = a.exerciseId == b.exerciseId
            override fun areContentsTheSame(a: RoutineExercise, b: RoutineExercise) = a == b
        }
    }
}
