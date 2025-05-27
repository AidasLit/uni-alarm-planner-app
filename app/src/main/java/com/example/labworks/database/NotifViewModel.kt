package com.example.labworks.database

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap.Title
import android.os.Build
import android.util.EventLogTags.Description
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.labworks.AlarmReceiver
import com.example.labworks.R
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.components.ComponentInstance
import com.example.labworks.database.data.components.DescriptionComp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings
import android.util.Log
import java.util.Date


// ViewModel for database access. A ViewModel is for separation of data and UI logic
// therefore this class is meant for Database access logic.
// It serves the function of communication between the database Repository class and UI
class NotifViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: NotifRepository = NotifRepository(
        NotifDatabase.getDatabase(application).notifDao()
    )

    fun addNotif(notif: Notif){
         viewModelScope.launch(Dispatchers.IO) {
             repository.addNotif(notif)
         }
    }

    fun addDescriptionComponent(notif: Notif, title: String, description: String){
        viewModelScope.launch(Dispatchers.IO) {
            val componentInstance : ComponentInstance = DescriptionComp(
                description = description
            )

            repository.addComponent(notif, componentInstance, title)
        }
    }

    suspend fun getAllNotifs() : List<Notif> {
        return repository.getAllNotifs()
    }

    fun deleteNotif(notif: Notif) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotif(notif)
        }
    }

    fun scheduleAlarm(context: Context, notif: Notif) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", notif.title)
            putExtra("description", notif.description ?: "")
            putExtra("notifId", notif.id)
            if (notif.repeatIntervalMillis != null) {
                putExtra("repeatIntervalMillis", notif.repeatIntervalMillis)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notif.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (notif.repeatIntervalMillis != null) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notif.timestamp,
                notif.repeatIntervalMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notif.timestamp,
                pendingIntent
            )
        }
    }


    fun addAndSchedule(context: Context, notif: Notif) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addNotifAndReturn(notif)
            val savedNotif = notif.copy(id = id)
            scheduleAlarm(context, savedNotif)
        }
    }



}

