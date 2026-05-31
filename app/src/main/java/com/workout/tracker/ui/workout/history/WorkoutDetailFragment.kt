package com.workout.tracker.ui.workout.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.workout.tracker.WorkoutApp
import com.workout.tracker.databinding.FragmentWorkoutDetailBinding
import com.workout.tracker.ui.adapter.WorkoutSetListAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WorkoutDetailFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private val args: WorkoutDetailFragmentArgs by navArgs()
    private val vm: WorkoutDetailViewModel by viewModels {
        WorkoutDetailViewModel.Factory(requireActivity().application as WorkoutApp, args.sessionId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = WorkoutSetListAdapter()
        binding.rvSets.adapter = adapter
        val df = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

        lifecycleScope.launch {
            vm.session.collect { session ->
                session ?: return@collect
                binding.tvName.text = session.routineName
                binding.tvDate.text = df.format(Date(session.startTime))
                val duration = session.endTime?.let {
                    val mins = TimeUnit.MILLISECONDS.toMinutes(it - session.startTime)
                    "${mins} min"
                } ?: "In progress"
                binding.tvDuration.text = duration
                binding.tvVolume.text = if (session.totalVolume > 0) "Total: %.0f kg".format(session.totalVolume) else ""
            }
        }
        lifecycleScope.launch {
            vm.sets.collect { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
