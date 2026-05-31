package com.workout.tracker.service

import android.app.*
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.workout.tracker.R
import com.workout.tracker.ui.MainActivity

class WorkoutTimerService : Service() {

    private val binder = LocalBinder()
    private var countdownTimer: CountDownTimer? = null
    var onTickListener: ((Long) -> Unit)? = null
    var onFinishListener: (() -> Unit)? = null

    inner class LocalBinder : Binder() {
        fun getService() = this@WorkoutTimerService
    }

    override fun onBind(intent: Intent) = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun startRestTimer(seconds: Long) {
        countdownTimer?.cancel()
        startForeground(NOTIF_ID, buildNotification("Rest timer: ${seconds}s"))
        countdownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisRemaining: Long) {
                val secs = millisRemaining / 1000
                onTickListener?.invoke(secs)
                updateNotification("Rest: ${secs}s remaining")
            }
            override fun onFinish() {
                onFinishListener?.invoke()
                vibrate()
                updateNotification("Rest complete — go!")
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }.start()
    }

    fun cancelTimer() {
        countdownTimer?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VibratorManager::class.java)
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Rest Timer", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Workout Tracker")
            .setContentText(text)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "rest_timer"
        const val NOTIF_ID = 1
    }
}
