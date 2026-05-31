package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.Routine
import com.workout.tracker.databinding.ItemRoutineBinding

class RoutineAdapter(
    private val onClick: (Routine) -> Unit,
    private val onLongClick: ((Routine) -> Unit)? = null
) : ListAdapter<Routine, RoutineAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemRoutineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val routine = getItem(position)
        holder.binding.tvName.text = routine.name
        holder.binding.tvDesc.text = routine.description.ifBlank { "Tap to start" }
        holder.binding.root.setOnClickListener { onClick(routine) }
        onLongClick?.let { handler ->
            holder.binding.root.setOnLongClickListener { handler(routine); true }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Routine>() {
            override fun areItemsTheSame(a: Routine, b: Routine) = a.id == b.id
            override fun areContentsTheSame(a: Routine, b: Routine) = a == b
        }
    }
}
