package com.workout.tracker.ui.running

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.*
import com.workout.tracker.WorkoutApp
import com.workout.tracker.bluetooth.BleManager
import com.workout.tracker.bluetooth.BleState
import com.workout.tracker.data.model.RunSession
import com.workout.tracker.data.repository.RunRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RunViewModel(application: Application, private val repo: RunRepository) : AndroidViewModel(application) {

    val bleManager = BleManager(application)
    val bleState: StateFlow<BleState> = bleManager.state
    val hrData = bleManager.hrData
    val scannedDevices = bleManager.scannedDevices

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

    private val _distanceKm = MutableStateFlow(0f)
    val distanceKm: StateFlow<Float> = _distanceKm

    private val _heartRates = mutableListOf<Int>()
    private var maxHr = 0
    private var runStartTime = 0L

    val recentRuns = repo.recentRuns.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: kotlinx.coroutines.Job? = null

    fun startRun() {
        _isRunning.value = true
        _elapsedSeconds.value = 0
        runStartTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                kotlinx.coroutines.delay(1000)
                _elapsedSeconds.value++
            }
        }
        viewModelScope.launch {
            hrData.collect { data ->
                if (_isRunning.value && data != null) {
                    _heartRates.add(data.heartRate)
                    if (data.heartRate > maxHr) maxHr = data.heartRate
                }
            }
        }
    }

    fun stopRun(distanceKm: Float, notes: String = "") {
        _isRunning.value = false
        timerJob?.cancel()
        val avgHr = if (_heartRates.isNotEmpty()) _heartRates.average().toInt() else 0
        viewModelScope.launch {
            repo.saveRun(
                RunSession(
                    startTime = runStartTime,
                    durationSeconds = _elapsedSeconds.value,
                    distanceKm = distanceKm,
                    avgHeartRate = avgHr,
                    maxHeartRate = maxHr,
                    notes = notes
                )
            )
        }
        _heartRates.clear()
        maxHr = 0
    }

    fun connectDevice(device: BluetoothDevice) = bleManager.connect(device)
    fun startScan() = bleManager.startScan()
    fun stopScan() = bleManager.stopScan()
    fun disconnectBle() = bleManager.disconnect()

    override fun onCleared() {
        bleManager.disconnect()
        super.onCleared()
    }

    class Factory(private val app: WorkoutApp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RunViewModel(app, app.runRepository) as T
        }
    }
}
