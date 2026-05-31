package com.workout.tracker.ui.workout.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentWorkoutHistoryBinding
import com.workout.tracker.ui.adapter.WorkoutHistoryAdapter
import kotlinx.coroutines.launch

class WorkoutHistoryFragment : Fragment() {

    private var _binding: FragmentWorkoutHistoryBinding? = null
    private val binding get() = _binding!!
    private val vm: WorkoutHistoryViewModel by viewModels {
        WorkoutHistoryViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = WorkoutHistoryAdapter { session ->
            findNavController().navigate(
                WorkoutHistoryFragmentDirections.actionHistoryToDetail(session.id)
            )
        }
        binding.rvHistory.adapter = adapter

        lifecycleScope.launch {
            vm.allSessions.collect {
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
