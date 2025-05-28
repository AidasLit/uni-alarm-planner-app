package com.example.labworks

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class AlarmActivity : Activity() {

    private var ringtone: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_alarm)

        val title = intent.getStringExtra("title") ?: "Alarm"
        val description = intent.getStringExtra("description") ?: "It's time!"

        findViewById<TextView>(R.id.alarmTitle).text = title
        findViewById<TextView>(R.id.alarmDescription).text = description

        val prefs: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val soundUriString = prefs.getString("alarmSound", null)
        val durationSeconds = prefs.getInt("alarmDuration", 10)
        val vibrationStrength = prefs.getLong("vibrationStrength", 300L)

        // Play ringtone
        val soundUri: Uri? = soundUriString?.let { Uri.parse(it) }
        ringtone = soundUri?.let { RingtoneManager.getRingtone(this, it) }
        ringtone?.play()

        // Start vibration if strength > 0
        if (vibrationStrength > 0L) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        // pause vibrate pause vibrate
                        longArrayOf(0, vibrationStrength, 250, vibrationStrength), 0 // -1 no loop 0 loops
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                // pause vibrate pause vibrate
                vibrator?.vibrate(longArrayOf(0, vibrationStrength, 250, vibrationStrength), 0) // -1 no loop 0 loops
            }
        }

        // Stop after user-defined time
        handler.postDelayed({
            stopAlarm()
            finish()
        }, durationSeconds * 1000L)

        // Dismiss button
        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            stopAlarm()
            finish()
        }
    }

    private fun stopAlarm() {
        if (ringtone?.isPlaying == true) {
            ringtone?.stop()
            println(" Ringtone stopped")
        } else {
            println(" Ringtone was not playing or null")
        }
        vibrator?.cancel()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
        handler.removeCallbacksAndMessages(null)
    }
}
