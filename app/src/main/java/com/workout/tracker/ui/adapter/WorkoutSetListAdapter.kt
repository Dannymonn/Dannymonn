package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.WorkoutSet
import com.workout.tracker.databinding.ItemSetBinding

class WorkoutSetListAdapter : ListAdapter<WorkoutSet, WorkoutSetListAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSetBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val set = getItem(position)
        holder.binding.tvExercise.text = set.exerciseName
        holder.binding.tvSetNumber.text = "Set ${set.setNumber}"
        holder.binding.tvReps.text = "${set.reps} reps"
        holder.binding.tvWeight.text = if (set.weightKg > 0) "× %.1f kg".format(set.weightKg) else ""
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<WorkoutSet>() {
            override fun areItemsTheSame(a: WorkoutSet, b: WorkoutSet) = a.id == b.id
            override fun areContentsTheSame(a: WorkoutSet, b: WorkoutSet) = a == b
        }
    }
}
