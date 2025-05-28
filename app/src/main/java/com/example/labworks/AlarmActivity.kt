package com.example.labworks

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.view.animation.AnimationUtils
import android.view.View


class AlarmActivity : Activity() {

    private var ringtone: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())
    private var vibrator: Vibrator? = null

    // Sensorius
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime = 0L

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

        // Groja garsą
        val soundUri: Uri? = soundUriString?.let { Uri.parse(it) }
        ringtone = soundUri?.let { RingtoneManager.getRingtone(this, it) }
        ringtone?.play()

        // Vibracija
        if (vibrationStrength > 0L) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, vibrationStrength, 250, vibrationStrength), 0
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, vibrationStrength, 250, vibrationStrength), 0)
            }
        }

        // Laiko ribojimas
        handler.postDelayed({
            stopAlarm()
            finish()
        }, durationSeconds * 1000L)

        // Mygtukas išjungti
        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            stopAlarm()
            finish()
        }

        // Jutiklis aktyvuojamas
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Start shake animation
        val rootLayout = findViewById<View>(R.id.activity_alarm)
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        rootLayout.startAnimation(shake)

    }

    private fun stopAlarm() {
        ringtone?.takeIf { it.isPlaying }?.stop()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
        handler.removeCallbacksAndMessages(null)
        sensorManager.unregisterListener(sensorListener)
    }

    // Shake listener
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val now = System.currentTimeMillis()

                if (acceleration > 15f && now - lastShakeTime > 1000) {
                    lastShakeTime = now
                    stopAlarm()
                    finish()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}
