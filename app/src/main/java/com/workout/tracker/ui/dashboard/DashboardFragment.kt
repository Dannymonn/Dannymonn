package com.workout.tracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.workout.tracker.R
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentDashboardBinding
import com.workout.tracker.ui.adapter.RoutineAdapter
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: DashboardViewModel by viewModels {
        DashboardViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val routineAdapter = RoutineAdapter { routine ->
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardToActiveWorkout(routine.id, routine.name)
            )
        }
        binding.rvRoutines.adapter = routineAdapter

        binding.btnFreeWorkout.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardToActiveWorkout(-1L, "Free Workout")
            )
        }
        binding.btnStartRun.setOnClickListener {
            findNavController().navigate(R.id.runFragment)
        }

        lifecycleScope.launch {
            vm.recentSessions.collect { sessions ->
                binding.tvLastWorkout.text = sessions.firstOrNull()?.routineName ?: "None yet"
            }
        }
        lifecycleScope.launch {
            vm.totalWorkouts.collect { count ->
                binding.tvTotalWorkouts.text = count.toString()
            }
        }
        lifecycleScope.launch {
            vm.totalVolume.collect { vol ->
                binding.tvTotalVolume.text = vol?.let { "%.0f kg".format(it) } ?: "0 kg"
            }
        }
        lifecycleScope.launch {
            vm.totalDistance.collect { dist ->
                binding.tvTotalDistance.text = dist?.let { "%.1f km".format(it) } ?: "0 km"
            }
        }
        lifecycleScope.launch {
            vm.latestGoal.collect { goal ->
                if (goal != null) {
                    binding.tvGoalWeight.text = "Goal: %.1f kg".format(goal.targetWeightKg)
                } else {
                    binding.tvGoalWeight.text = "No goal set"
                }
            }
        }
        lifecycleScope.launch {
            vm.latestWeight.collect { entry ->
                binding.tvCurrentWeight.text = entry?.let { "Current: %.1f kg".format(it.weightKg) } ?: "Log your weight"
            }
        }
        lifecycleScope.launch {
            vm.allRoutines.collect { routines ->
                routineAdapter.submitList(routines)
                binding.tvNoRoutines.visibility = if (routines.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
