package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.databinding.ItemActiveExerciseBinding
import com.workout.tracker.ui.workout.active.ActiveExercise

class ActiveExerciseAdapter(
    private val onLogSet: (exerciseId: Long, reps: Int, weight: Float, rest: Int) -> Unit,
    private val onDeleteSet: (exerciseId: Long) -> Unit,
    private val onRemoveExercise: (exerciseId: Long) -> Unit
) : ListAdapter<ActiveExercise, ActiveExerciseAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemActiveExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemActiveExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val ex = item.exercise
        holder.binding.tvExerciseName.text = ex.name
        holder.binding.tvMuscle.text = ex.muscleGroup.name
        holder.binding.weightLayout.visibility = if (ex.usesWeight) View.VISIBLE else View.GONE
        holder.binding.tvSetCount.text = "${item.sets.size} sets logged"

        val setsText = item.sets.joinToString("\n") { s ->
            if (ex.usesWeight) "Set ${s.setNumber}: ${s.reps} reps × %.1f kg".format(s.weightKg)
            else "Set ${s.setNumber}: ${s.reps} reps"
        }
        holder.binding.tvSets.text = setsText.ifBlank { "No sets yet" }

        holder.binding.btnAddSet.setOnClickListener {
            val reps = holder.binding.etReps.text?.toString()?.toIntOrNull() ?: 0
            val weight = if (ex.usesWeight) holder.binding.etWeight.text?.toString()?.toFloatOrNull() ?: 0f else 0f
            val rest = holder.binding.etRest.text?.toString()?.toIntOrNull() ?: 90
            if (reps > 0) onLogSet(ex.id, reps, weight, rest)
        }

        holder.binding.btnDeleteSet.setOnClickListener { onDeleteSet(ex.id) }
        holder.binding.btnRemoveExercise.setOnClickListener { onRemoveExercise(ex.id) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ActiveExercise>() {
            override fun areItemsTheSame(a: ActiveExercise, b: ActiveExercise) = a.exercise.id == b.exercise.id
            override fun areContentsTheSame(a: ActiveExercise, b: ActiveExercise) = a == b
        }
    }
}
