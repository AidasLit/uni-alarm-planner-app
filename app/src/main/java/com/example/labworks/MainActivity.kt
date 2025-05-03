package com.example.labworks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

class MainActivity : AppCompatActivity() {

    private lateinit var compassArrow: ImageView
    private var currentDegree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        setupBottomNavigation()
        showSplashScreen(savedInstanceState)
        trySetupCompass()




    }

    // 🔔 Sukuria notifikacijų kanalą (būtina Android 8+)
    private fun createNotificationChannel() {
        val channelId = "0"
        val channelName = "Main"
        val descriptionText = "Main channel used for app notifications"

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // 📲 Bottom Navigation setup
    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_month_calendar -> MonthCalendarFragment()
                R.id.nav_week_calandar -> WeekCalendarFragment()
                R.id.nav_alarm -> AlarmFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } ?: false
        }
    }

    // ⏳ Parodo splash screen'ą 1.5s ir užkrauna pradžios fragmentą
    private fun showSplashScreen(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val splashView = findViewById<FrameLayout>(R.id.splash_view)
            splashView.visibility = View.VISIBLE
            splashView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in))

            Handler(Looper.getMainLooper()).postDelayed({
                splashView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
                splashView.visibility = View.GONE

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, WeekCalendarFragment())
                    .commit()

                val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
                navView.selectedItemId = R.id.nav_week_calandar
            }, 1500L)
        }
    }

    // 🧭 Paleidžia rodyklės animaciją, jei komponentas yra UI
    private fun trySetupCompass() {
        try {
            compassArrow = findViewById(R.id.arrow_image)
            startCompassRotation()
        } catch (e: Exception) {
            // Jei nėra rodyklės, nieko nedarom
        }
    }

    private fun startCompassRotation() {
        val rotateAnimation = RotateAnimation(
            0f, 720f,  // 2 pilni apsisukimai
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 2000  // Laiką gali koreguoti – pvz., 2000 jei nori truputį lėčiau
        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimation.fillAfter = true

        compassArrow.startAnimation(rotateAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val splashView = findViewById<FrameLayout>(R.id.splash_view)
            splashView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out))
            splashView.visibility = View.GONE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeekCalendarFragment())
                .commit()

            val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
            navView.selectedItemId = R.id.nav_week_calandar
        }, 1500)
    }










    // 🔔 Paleidžia notifikaciją, kai leidimas duotas
    private fun throwNotification(notificationId: Int) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, NotifManage::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "0")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Counter maxed out")
            .setContentText("The counter reached its maximum number - 3")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
        startActivity(Intent(this, NotifManage::class.java))
    }
}
