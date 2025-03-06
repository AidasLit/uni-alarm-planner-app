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
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val CHANNEL_ID = "0"
        val button : Button = findViewById(R.id.button)
        val counter : TextView = findViewById(R.id.textView)

        var counterNumber = 0
        counter.text = counterNumber.toString()

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
            .setContentText("The counter reached it's maximum number - 10")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        button.setOnClickListener{
            if(counterNumber < 10) {
                counterNumber++
                counter.text = counterNumber.toString()
            }
            else{
                throwNotification(1, builder)
            }
        }
    }

    private fun throwNotification(notificationId : Int, builder : NotificationCompat.Builder){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

    private fun getNotificationBuilder(contentIntent : PendingIntent ,title : String = "blank", text : String = "blank", icon : Int = R.drawable.ic_launcher_background){

    }
}