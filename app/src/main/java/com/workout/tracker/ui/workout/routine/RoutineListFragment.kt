package com.workout.tracker.ui.workout.routine

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentRoutineListBinding
import com.workout.tracker.ui.adapter.RoutineAdapter
import kotlinx.coroutines.launch

class RoutineListFragment : Fragment() {

    private var _binding: FragmentRoutineListBinding? = null
    private val binding get() = _binding!!
    private val vm: RoutineListViewModel by viewModels {
        RoutineListViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoutineListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = RoutineAdapter(
            onClick = { routine ->
                findNavController().navigate(
                    RoutineListFragmentDirections.actionRoutinesToEditRoutine(routine.id, routine.name)
                )
            },
            onLongClick = { routine ->
                vm.deleteRoutine(routine)
            }
        )
        binding.rvRoutines.adapter = adapter

        binding.fabAddRoutine.setOnClickListener {
            findNavController().navigate(
                RoutineListFragmentDirections.actionRoutinesToEditRoutine(-1L, "New Routine")
            )
        }

        lifecycleScope.launch {
            vm.routines.collect {
                adapter.submitList(it)
                binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
