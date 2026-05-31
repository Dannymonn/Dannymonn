package com.workout.tracker.bluetooth

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

data class BleHeartRateData(val heartRate: Int, val cadence: Int = 0)

sealed class BleState {
    object Disconnected : BleState()
    object Scanning : BleState()
    data class Connected(val deviceName: String) : BleState()
    data class Error(val message: String) : BleState()
}

class BleManager(private val context: Context) {

    private val adapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val scanner get() = adapter?.bluetoothLeScanner

    private var gatt: BluetoothGatt? = null
    private val _state = MutableStateFlow<BleState>(BleState.Disconnected)
    val state: StateFlow<BleState> = _state

    private val _hrData = MutableStateFlow<BleHeartRateData?>(null)
    val hrData: StateFlow<BleHeartRateData?> = _hrData

    val scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (scannedDevices.value.none { it.address == device.address }) {
                scannedDevices.value = scannedDevices.value + device
            }
        }
        override fun onScanFailed(errorCode: Int) {
            _state.value = BleState.Error("Scan failed: $errorCode")
        }
    }

    fun startScan() {
        scannedDevices.value = emptyList()
        _state.value = BleState.Scanning
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(HR_SERVICE))
            .build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner?.startScan(listOf(filter), settings, scanCallback)
    }

    fun stopScan() {
        scanner?.stopScan(scanCallback)
        if (_state.value is BleState.Scanning) _state.value = BleState.Disconnected
    }

    fun connect(device: BluetoothDevice) {
        stopScan()
        gatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _state.value = BleState.Disconnected
        _hrData.value = null
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _state.value = BleState.Connected(g.device.name ?: g.device.address)
                    g.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _state.value = BleState.Disconnected
                    g.close()
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val hrChar = g.getService(HR_SERVICE)?.getCharacteristic(HR_MEASUREMENT) ?: return
            g.setCharacteristicNotification(hrChar, true)
            val desc = hrChar.getDescriptor(CLIENT_CHAR_CONFIG)
            desc?.let {
                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                g.writeDescriptor(it)
            }
        }

        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == HR_MEASUREMENT) {
                val data = characteristic.value ?: return
                val flags = data[0].toInt()
                val hrFormat = flags and 0x01
                val hr = if (hrFormat == 0) data[1].toInt() and 0xFF
                else ((data[2].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
                _hrData.value = BleHeartRateData(heartRate = hr)
            }
        }
    }

    companion object {
        val HR_SERVICE: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")
        val HR_MEASUREMENT: UUID = UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB")
        val CLIENT_CHAR_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
    }
}
