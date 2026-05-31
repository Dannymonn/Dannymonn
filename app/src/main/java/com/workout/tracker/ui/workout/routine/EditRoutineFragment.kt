package com.workout.tracker.ui.workout.routine

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentEditRoutineBinding
import com.workout.tracker.ui.adapter.RoutineExerciseEditAdapter
import com.workout.tracker.ui.exercise.ExercisePickerDialog
import kotlinx.coroutines.launch

class EditRoutineFragment : Fragment() {

    private var _binding: FragmentEditRoutineBinding? = null
    private val binding get() = _binding!!
    private val args: EditRoutineFragmentArgs by navArgs()
    private val vm: EditRoutineViewModel by viewModels {
        EditRoutineViewModel.Factory(requireActivity().application as WorkoutApp, args.routineId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etRoutineName.setText(args.routineName)

        val adapter = RoutineExerciseEditAdapter(
            onRemove = { vm.removeExercise(it) },
            onSetsChanged = { re, sets -> vm.updateTargetSets(re, sets) },
            onRepsChanged = { re, reps -> vm.updateTargetReps(re, reps) }
        )
        binding.rvExercises.adapter = adapter

        binding.btnAddExercise.setOnClickListener {
            ExercisePickerDialog.show(childFragmentManager) { exercise ->
                vm.addExercise(exercise)
            }
        }

        binding.btnSaveRoutine.setOnClickListener {
            val name = binding.etRoutineName.text?.toString()?.trim() ?: ""
            if (name.isBlank()) {
                binding.etRoutineName.error = "Name required"
                return@setOnClickListener
            }
            vm.save(name)
            findNavController().navigateUp()
        }

        lifecycleScope.launch {
            vm.routineExercises.collect { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
