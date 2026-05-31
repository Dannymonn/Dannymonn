package com.workout.tracker.ui.exercise

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.ExerciseCategory
import com.workout.tracker.data.model.MuscleGroup
import com.workout.tracker.databinding.FragmentExerciseLibraryBinding
import com.workout.tracker.databinding.DialogAddExerciseBinding
import com.workout.tracker.ui.adapter.ExercisePickAdapter
import kotlinx.coroutines.launch

class ExerciseLibraryFragment : Fragment() {

    private var _binding: FragmentExerciseLibraryBinding? = null
    private val binding get() = _binding!!
    private val vm: ExerciseLibraryViewModel by viewModels {
        ExerciseLibraryViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExercisePickAdapter(
            onLongClick = { exercise ->
                if (exercise.isCustom) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete ${exercise.name}?")
                        .setPositiveButton("Delete") { _, _ -> vm.deleteExercise(exercise) }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        )
        binding.rvExercises.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { vm.search(q ?: "") }
            override fun onQueryTextChange(q: String?) = true.also { vm.search(q ?: "") }
        })

        binding.fabAddExercise.setOnClickListener { showAddExerciseDialog() }

        lifecycleScope.launch {
            vm.exercises.collect { adapter.submitList(it) }
        }
    }

    private fun showAddExerciseDialog() {
        val dialogBinding = DialogAddExerciseBinding.inflate(layoutInflater)
        val muscleGroups = MuscleGroup.values()
        val categories = ExerciseCategory.values()
        dialogBinding.spinnerMuscle.adapter = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, muscleGroups.map { it.name }
        )
        dialogBinding.spinnerCategory.adapter = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, categories.map { it.name }
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Add Custom Exercise")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etName.text?.toString()?.trim() ?: ""
                if (name.isNotBlank()) {
                    val muscle = muscleGroups[dialogBinding.spinnerMuscle.selectedItemPosition]
                    val cat = categories[dialogBinding.spinnerCategory.selectedItemPosition]
                    val usesWeight = dialogBinding.cbUsesWeight.isChecked
                    vm.addCustomExercise(name, muscle, cat, usesWeight)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
