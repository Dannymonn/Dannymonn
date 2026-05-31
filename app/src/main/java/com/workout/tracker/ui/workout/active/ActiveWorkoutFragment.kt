package com.workout.tracker.ui.workout.active

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentActiveWorkoutBinding
import com.workout.tracker.service.WorkoutTimerService
import com.workout.tracker.ui.adapter.ActiveExerciseAdapter
import com.workout.tracker.ui.exercise.ExercisePickerDialog
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ActiveWorkoutFragment : Fragment() {

    private var _binding: FragmentActiveWorkoutBinding? = null
    private val binding get() = _binding!!
    private val args: ActiveWorkoutFragmentArgs by navArgs()

    private val vm: ActiveWorkoutViewModel by viewModels {
        ActiveWorkoutViewModel.Factory(requireActivity().application as WorkoutApp, args.routineId, args.routineName)
    }

    private var timerService: WorkoutTimerService? = null
    private var timerBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            timerService = (binder as WorkoutTimerService.LocalBinder).getService()
            timerBound = true
            setupTimerCallbacks()
        }
        override fun onServiceDisconnected(name: ComponentName) { timerBound = false }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActiveWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvWorkoutName.text = args.routineName

        val exerciseAdapter = ActiveExerciseAdapter(
            onLogSet = { exerciseId, reps, weight, rest ->
                vm.logSet(exerciseId, reps, weight, rest)
            },
            onDeleteSet = { exerciseId -> vm.deleteLastSet(exerciseId) },
            onRemoveExercise = { exerciseId -> vm.removeExercise(exerciseId) }
        )
        binding.rvExercises.adapter = exerciseAdapter

        binding.btnAddExercise.setOnClickListener {
            ExercisePickerDialog.show(childFragmentManager) { exercise ->
                vm.addExercise(exercise)
            }
        }

        binding.btnFinishWorkout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Finish Workout?")
                .setMessage("This will save and complete your session.")
                .setPositiveButton("Finish") { _, _ ->
                    vm.finishWorkout()
                    findNavController().navigateUp()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        lifecycleScope.launch {
            vm.activeExercises.collect { exercises ->
                exerciseAdapter.submitList(exercises.toList())
                binding.tvEmptyHint.visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            vm.restTimerSeconds.collect { secs ->
                if (secs != null && timerBound) {
                    timerService?.startRestTimer(secs)
                }
            }
        }

        // Elapsed workout timer
        val startTime = vm.workoutStartTime
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val elapsedRunnable = object : Runnable {
            override fun run() {
                if (_binding == null) return
                val elapsed = System.currentTimeMillis() - startTime
                val mins = TimeUnit.MILLISECONDS.toMinutes(elapsed)
                val secs = TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60
                binding.tvElapsed.text = "%02d:%02d".format(mins, secs)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(elapsedRunnable)

        bindTimerService()
    }

    private fun setupTimerCallbacks() {
        timerService?.onTickListener = { secs ->
            requireActivity().runOnUiThread {
                _binding?.tvRestTimer?.text = "Rest: ${secs}s"
                _binding?.tvRestTimer?.visibility = View.VISIBLE
            }
        }
        timerService?.onFinishListener = {
            requireActivity().runOnUiThread {
                _binding?.tvRestTimer?.text = "Go!"
                vm.clearRestTimer()
            }
        }
    }

    private fun bindTimerService() {
        val intent = Intent(requireContext(), WorkoutTimerService::class.java)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        if (timerBound) requireContext().unbindService(serviceConnection)
        super.onDestroyView()
        _binding = null
    }
}
