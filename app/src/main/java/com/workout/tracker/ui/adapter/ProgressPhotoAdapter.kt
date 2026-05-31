package com.workout.tracker.ui.adapter

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.workout.tracker.data.model.ProgressPhoto
import com.workout.tracker.databinding.ItemProgressPhotoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProgressPhotoAdapter(
    private val onLongClick: (ProgressPhoto) -> Unit
) : ListAdapter<ProgressPhoto, ProgressPhotoAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemProgressPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProgressPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val photo = getItem(position)
        Glide.with(holder.binding.root)
            .load(File(photo.filePath))
            .centerCrop()
            .into(holder.binding.ivPhoto)
        holder.binding.tvDate.text = dateFormat.format(Date(photo.takenAt))
        holder.binding.tvAngle.text = photo.angle.name
        holder.binding.tvWeight.text = photo.weightAtTime?.let { "%.1f kg".format(it) } ?: ""
        holder.binding.root.setOnLongClickListener { onLongClick(photo); true }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ProgressPhoto>() {
            override fun areItemsTheSame(a: ProgressPhoto, b: ProgressPhoto) = a.id == b.id
            override fun areContentsTheSame(a: ProgressPhoto, b: ProgressPhoto) = a == b
        }
    }
}
