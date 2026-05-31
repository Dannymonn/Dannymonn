package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.WorkoutSession
import com.workout.tracker.databinding.ItemWorkoutHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WorkoutHistoryAdapter(
    private val onClick: (WorkoutSession) -> Unit
) : ListAdapter<WorkoutSession, WorkoutHistoryAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemWorkoutHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemWorkoutHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val session = getItem(position)
        holder.binding.tvName.text = session.routineName
        holder.binding.tvDate.text = dateFormat.format(Date(session.startTime))
        val duration = session.endTime?.let {
            val mins = TimeUnit.MILLISECONDS.toMinutes(it - session.startTime)
            "${mins} min"
        } ?: "In progress"
        holder.binding.tvDuration.text = duration
        holder.binding.tvVolume.text = if (session.totalVolume > 0) "%.0f kg total".format(session.totalVolume) else ""
        holder.binding.root.setOnClickListener { onClick(session) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<WorkoutSession>() {
            override fun areItemsTheSame(a: WorkoutSession, b: WorkoutSession) = a.id == b.id
            override fun areContentsTheSame(a: WorkoutSession, b: WorkoutSession) = a == b
        }
    }
}
