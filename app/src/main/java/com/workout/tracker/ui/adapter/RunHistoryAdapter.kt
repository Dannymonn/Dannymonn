package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.workout.tracker.data.model.RunSession
import com.workout.tracker.databinding.ItemRunHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RunHistoryAdapter : ListAdapter<RunSession, RunHistoryAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemRunHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRunHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val run = getItem(position)
        holder.binding.tvDate.text = dateFormat.format(Date(run.startTime))
        holder.binding.tvDistance.text = "%.2f km".format(run.distanceKm)
        val mins = TimeUnit.SECONDS.toMinutes(run.durationSeconds)
        val secs = run.durationSeconds % 60
        holder.binding.tvDuration.text = "%d:%02d".format(mins, secs)
        holder.binding.tvPace.text = if (run.paceMinPerKm > 0) "%.1f min/km".format(run.paceMinPerKm) else ""
        holder.binding.tvHr.text = if (run.avgHeartRate > 0) "Avg HR: ${run.avgHeartRate} bpm" else ""
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RunSession>() {
            override fun areItemsTheSame(a: RunSession, b: RunSession) = a.id == b.id
            override fun areContentsTheSame(a: RunSession, b: RunSession) = a == b
        }
    }
}
