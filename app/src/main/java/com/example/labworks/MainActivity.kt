package com.example.labworks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var currentTabIndex = 0
    private lateinit var compassArrow: ImageView
    private lateinit var navView: BottomNavigationView

    private val tabOrder = listOf(
        R.id.nav_month_calendar,
        R.id.nav_week_calandar,
        R.id.nav_alarm,
        R.id.nav_settings
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        navView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()
        showSplashScreen(savedInstanceState)
        trySetupCompass()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "0",
            "Main",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Main channel used for app notifications"
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupBottomNavigation() {
        navView.setOnItemSelectedListener { item ->
            val newTabIndex = tabOrder.indexOf(item.itemId)
            if (newTabIndex == -1) return@setOnItemSelectedListener false

            val transaction = supportFragmentManager.beginTransaction()
            if (newTabIndex > currentTabIndex) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            } else if (newTabIndex < currentTabIndex) {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            currentTabIndex = newTabIndex

            val fragment = when (item.itemId) {
                R.id.nav_month_calendar -> MonthCalendarFragment()
                R.id.nav_week_calandar -> WeekCalendarFragment()
                R.id.nav_alarm -> AlarmFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> return@setOnItemSelectedListener false
            }

            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()

            true
        }
    }

    private fun showSplashScreen(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val splashView = findViewById<FrameLayout>(R.id.splash_view)
            splashView.visibility = View.VISIBLE
            splashView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

            Handler(Looper.getMainLooper()).postDelayed({
                splashView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
                splashView.visibility = View.GONE

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MonthCalendarFragment())
                    .commit()

                navView.selectedItemId = R.id.nav_month_calendar
            }, 1500L)
        }
    }

    private fun trySetupCompass() {
        try {
            compassArrow = findViewById(R.id.arrow_image)
            startCompassRotation()
        } catch (e: Exception) {
            // Ignore if arrow not present
        }
    }

    private fun startCompassRotation() {
        val rotateAnimation = RotateAnimation(
            0f, 720f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 2000
        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotateAnimation.fillAfter = true

        compassArrow.startAnimation(rotateAnimation)
    }

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

        val pendingIntent = PendingIntent.getActivity(
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
    }
}
