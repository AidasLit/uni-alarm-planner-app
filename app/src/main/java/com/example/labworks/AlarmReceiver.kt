package com.example.labworks

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Alarm"
        val description = intent?.getStringExtra("description") ?: "It's time!"

        Log.d("AlarmReceiver", "Alarm fired: $title - $description")

        // Load custom sound URI from SharedPreferences
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val soundUriString = prefs.getString("alarmSound", null)
        val soundUri: Uri? = soundUriString?.let { Uri.parse(it) }

        // Use dynamic channel ID based on sound
        val channelId = "alarm_channel_${soundUri?.hashCode() ?: "default"}"

        // Prepare full-screen intent
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("title", title)
            putExtra("description", description)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setDescription("Channel for alarm notifications")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                soundUri?.let {
                    setSound(it, Notification.AUDIO_ATTRIBUTES_DEFAULT)
                }
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setFullScreenIntent(fullScreenPendingIntent, true)

        // Set custom sound (for pre-Oreo or fallback)
        soundUri?.let { notificationBuilder.setSound(it) }

        val notification = notificationBuilder.build()

        // Show notification
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
            Log.d("AlarmReceiver", "Notification displayed!")
        } else {
            Log.e("AlarmReceiver", "Notification not shown: missing POST_NOTIFICATIONS permission")
        }

        // Launch full-screen alarm activity
        context.startActivity(fullScreenIntent)

        // Handle repeating alarm
        val repeatInterval = intent?.getLongExtra("repeatIntervalMillis", -1L) ?: -1L
        val notifId = intent?.getLongExtra("notifId", -1L) ?: -1L

        if (repeatInterval > 0 && notifId >= 0) {
            val nextTriggerTime = System.currentTimeMillis() + repeatInterval
            val repeatIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("description", description)
                putExtra("notifId", notifId)
                putExtra("repeatIntervalMillis", repeatInterval)
            }

            val repeatPendingIntent = PendingIntent.getBroadcast(
                context,
                notifId.toInt(),
                repeatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTriggerTime,
                repeatPendingIntent
            )

            Log.d("AlarmReceiver", "Next repeating alarm scheduled for: $nextTriggerTime")
        }
    }
}
