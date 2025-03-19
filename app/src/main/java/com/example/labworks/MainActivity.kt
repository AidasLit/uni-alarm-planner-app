package com.example.labworks

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.labworks.settings.AppSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val CHANNEL_ID = "0"
        val button : Button = findViewById(R.id.button)
        val counter : TextView = findViewById(R.id.textView)

        var counterNumber = 0
        counter.text = counterNumber.toString()

        val button_settings : Button = findViewById(R.id.button10)

        // Create the NotificationChannel.
        val name = "Main"
        val descriptionText = "Main channel used for app notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        // Create an explicit intent for an Activity in your app.
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Counter maxed out")
            .setContentText("The counter reached it's maximum number - 11")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        button.setOnClickListener{
            if(counterNumber < 11) {
                counterNumber++
                counter.text = counterNumber.toString()
            }
            else{
                throwNotification(1, builder)
            }
        }
        button_settings.setOnClickListener{
            val intent = Intent(this, AppSettings::class.java)
            startActivity(intent)
        }

    }

    private fun throwNotification(notificationId : Int, builder : NotificationCompat.Builder){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            var requestCode = 1
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
            return
        }
        else{
            println("trying to launch notif")
            NotificationManagerCompat.from(this).notify(notificationId, builder.build())
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}