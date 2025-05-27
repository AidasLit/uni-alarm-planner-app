package com.example.labworks

import android.app.Activity
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class AlarmActivity : Activity() {

    private var ringtone: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())

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

        //  Play ringtone only here
        val soundUri: Uri? = soundUriString?.let { Uri.parse(it) }
        ringtone = soundUri?.let { RingtoneManager.getRingtone(this, it) }
        ringtone?.play()

        //  Stop after user-defined time
        handler.postDelayed({
            stopRingtone()
            finish()
        }, durationSeconds * 1000L)

        //  Dismiss button
        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            stopRingtone()
            finish()
        }
    }

    private fun stopRingtone() {
        if (ringtone?.isPlaying == true) {
            ringtone?.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        handler.removeCallbacksAndMessages(null)
    }
}
