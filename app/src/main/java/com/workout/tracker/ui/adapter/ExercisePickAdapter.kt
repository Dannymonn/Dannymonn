package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.Exercise
import com.workout.tracker.databinding.ItemExerciseBinding

class ExercisePickAdapter(
    private val onClick: ((Exercise) -> Unit)? = null,
    private val onLongClick: ((Exercise) -> Unit)? = null
) : ListAdapter<Exercise, ExercisePickAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ex = getItem(position)
        holder.binding.tvName.text = ex.name
        holder.binding.tvMuscle.text = ex.muscleGroup.name
        holder.binding.tvCategory.text = ex.category.name
        holder.binding.tvCustom.visibility = if (ex.isCustom) View.VISIBLE else View.GONE
        onClick?.let { holder.binding.root.setOnClickListener { it(ex) } }
        onLongClick?.let { handler ->
            holder.binding.root.setOnLongClickListener { handler(ex); true }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(a: Exercise, b: Exercise) = a.id == b.id
            override fun areContentsTheSame(a: Exercise, b: Exercise) = a == b
        }
    }
}
