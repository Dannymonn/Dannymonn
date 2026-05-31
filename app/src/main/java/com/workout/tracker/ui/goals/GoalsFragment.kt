package com.workout.tracker.ui.goals

import android.Manifest
import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.flow.combine
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.PhotoAngle
import com.workout.tracker.databinding.DialogSetGoalBinding
import com.workout.tracker.databinding.DialogLogWeightBinding
import com.workout.tracker.databinding.FragmentGoalsBinding
import com.workout.tracker.ui.adapter.ProgressPhotoAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val vm: GoalsViewModel by viewModels {
        GoalsViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    private var currentPhotoPath: String? = null
    private var selectedAngle = PhotoAngle.FRONT

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                vm.savePhoto(path, selectedAngle, vm.latestWeight.value?.weightKg)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val photoAdapter = ProgressPhotoAdapter { photo ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete photo?")
                .setPositiveButton("Delete") { _, _ -> vm.deletePhoto(photo) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.rvPhotos.adapter = photoAdapter

        binding.btnSetGoal.setOnClickListener { showSetGoalDialog() }
        binding.btnLogWeight.setOnClickListener { showLogWeightDialog() }
        binding.btnTakePhoto.setOnClickListener { takeProgressPhoto() }

        lifecycleScope.launch {
            vm.latestGoal.collect { goal ->
                if (goal != null) {
                    binding.tvGoalWeight.text = "Target: %.1f kg".format(goal.targetWeightKg)
                } else {
                    binding.tvGoalWeight.text = "No goal set yet"
                }
            }
        }
        lifecycleScope.launch {
            vm.latestWeight.collect { entry ->
                binding.tvCurrentWeight.text = entry?.let { "Current: %.1f kg".format(it.weightKg) } ?: "No weight logged"
            }
        }
        lifecycleScope.launch {
            vm.allPhotos.collect { photos ->
                photoAdapter.submitList(photos)
                binding.tvNoPhotos.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            vm.latestGoal.combine(vm.latestWeight) { goal, weight ->
                Pair(goal, weight)
            }.collect { (goal, weight) ->
                if (goal != null && weight != null) {
                    val progress = ((weight.weightKg - goal.currentWeightKg) /
                        (goal.targetWeightKg - goal.currentWeightKg) * 100).coerceIn(0f, 100f)
                    binding.progressGoal.progress = progress.toInt()
                    binding.tvProgress.text = "%.0f%% to goal".format(progress)
                }
            }
        }
    }

    private fun showSetGoalDialog() {
        val dialogBinding = DialogSetGoalBinding.inflate(layoutInflater)
        vm.latestGoal.value?.let {
            dialogBinding.etTargetWeight.setText("%.1f".format(it.targetWeightKg))
            dialogBinding.etCurrentWeight.setText("%.1f".format(it.currentWeightKg))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Set Weight Goal")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val target = dialogBinding.etTargetWeight.text?.toString()?.toFloatOrNull() ?: return@setPositiveButton
                val current = dialogBinding.etCurrentWeight.text?.toString()?.toFloatOrNull() ?: return@setPositiveButton
                vm.setGoal(target, current)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogWeightDialog() {
        val dialogBinding = DialogLogWeightBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Log Weight")
            .setView(dialogBinding.root)
            .setPositiveButton("Log") { _, _ ->
                val weight = dialogBinding.etWeight.text?.toString()?.toFloatOrNull() ?: return@setPositiveButton
                val notes = dialogBinding.etNotes.text?.toString() ?: ""
                vm.logWeight(weight, notes)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun takeProgressPhoto() {
        val angles = PhotoAngle.values()
        AlertDialog.Builder(requireContext())
            .setTitle("Photo angle")
            .setItems(angles.map { it.name }.toTypedArray()) { _, i ->
                selectedAngle = angles[i]
                launchCamera()
            }
            .show()
    }

    private fun launchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 200)
            return
        }
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoUri: Uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        takePicture.launch(intent)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PROGRESS_${timestamp}_", ".jpg", dir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
