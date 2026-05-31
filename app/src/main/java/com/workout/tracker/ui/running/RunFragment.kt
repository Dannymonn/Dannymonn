package com.workout.tracker.ui.running

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.workout.tracker.WorkoutApp
import com.workout.tracker.bluetooth.BleState
import com.workout.tracker.databinding.FragmentRunBinding
import com.workout.tracker.ui.adapter.RunHistoryAdapter
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RunFragment : Fragment() {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!
    private val vm: RunViewModel by viewModels {
        RunViewModel.Factory(requireActivity().application as WorkoutApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val historyAdapter = RunHistoryAdapter()
        binding.rvRunHistory.adapter = historyAdapter

        binding.btnStartRun.setOnClickListener {
            vm.startRun()
            binding.btnStartRun.visibility = View.GONE
            binding.btnStopRun.visibility = View.VISIBLE
            binding.layoutRunActive.visibility = View.VISIBLE
        }

        binding.btnStopRun.setOnClickListener {
            val distanceStr = binding.etDistance.text?.toString() ?: "0"
            val distance = distanceStr.toFloatOrNull() ?: 0f
            val notes = binding.etRunNotes.text?.toString() ?: ""
            vm.stopRun(distance, notes)
            binding.btnStartRun.visibility = View.VISIBLE
            binding.btnStopRun.visibility = View.GONE
            binding.layoutRunActive.visibility = View.GONE
            binding.etDistance.text?.clear()
        }

        binding.btnConnectSensor.setOnClickListener {
            if (hasBlePermissions()) showBleScanDialog()
            else requestBlePermissions()
        }

        lifecycleScope.launch {
            vm.elapsedSeconds.collect { secs ->
                val m = TimeUnit.SECONDS.toMinutes(secs)
                val s = secs % 60
                binding.tvTimer.text = "%02d:%02d".format(m, s)
            }
        }
        lifecycleScope.launch {
            vm.hrData.collect { data ->
                binding.tvHeartRate.text = data?.let { "${it.heartRate} bpm" } ?: "-- bpm"
            }
        }
        lifecycleScope.launch {
            vm.bleState.collect { state ->
                binding.tvBleStatus.text = when (state) {
                    is BleState.Disconnected -> "Sensor: Not connected"
                    is BleState.Scanning -> "Scanning..."
                    is BleState.Connected -> "Connected: ${state.deviceName}"
                    is BleState.Error -> "Error: ${state.message}"
                }
            }
        }
        lifecycleScope.launch {
            vm.recentRuns.collect { runs ->
                historyAdapter.submitList(runs)
            }
        }
    }

    private fun showBleScanDialog() {
        vm.startScan()
        val devices = mutableListOf<BluetoothDevice>()
        val namesAdapter = android.widget.ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select HR Sensor")
            .setAdapter(namesAdapter) { _, idx ->
                vm.connectDevice(devices[idx])
            }
            .setNegativeButton("Cancel") { _, _ -> vm.stopScan() }
            .create()

        lifecycleScope.launch {
            vm.scannedDevices.collect { list ->
                devices.clear()
                devices.addAll(list)
                namesAdapter.clear()
                namesAdapter.addAll(list.map { it.name ?: it.address })
                namesAdapter.notifyDataSetChanged()
            }
        }
        dialog.show()
    }

    private fun hasBlePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBlePermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
        requestPermissions(perms, 101)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
