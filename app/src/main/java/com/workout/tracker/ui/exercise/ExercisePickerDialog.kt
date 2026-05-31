package com.workout.tracker.ui.exercise

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.workout.tracker.WorkoutApp
import com.workout.tracker.data.model.Exercise
import com.workout.tracker.databinding.DialogExercisePickerBinding
import com.workout.tracker.ui.adapter.ExercisePickAdapter
import kotlinx.coroutines.launch

class ExercisePickerDialog : DialogFragment() {

    private var _binding: DialogExercisePickerBinding? = null
    private val binding get() = _binding!!
    private val vm: ExerciseLibraryViewModel by viewModels {
        ExerciseLibraryViewModel.Factory(requireActivity().application as WorkoutApp)
    }
    var onExercisePicked: ((Exercise) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogExercisePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExercisePickAdapter { exercise ->
            onExercisePicked?.invoke(exercise)
            dismiss()
        }
        binding.rvExercises.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { vm.search(q ?: "") }
            override fun onQueryTextChange(q: String?) = true.also { vm.search(q ?: "") }
        })

        lifecycleScope.launch {
            vm.exercises.collect { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun show(fm: FragmentManager, onPicked: (Exercise) -> Unit) {
            ExercisePickerDialog().apply {
                onExercisePicked = onPicked
            }.show(fm, "exercise_picker")
        }
    }
}
