package com.example.labworks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.labworks.database.data.Notif
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifCreateScreen(
    onDone: (Notif) -> Unit,
    onBack: () -> Unit,
    onLaunchMap: () -> Unit,
    passedLat: Double? = null,
    passedLng: Double? = null
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var dateText by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        )
    }
    var timeText by remember {
        mutableStateOf(
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        )
    }

    val selectedLat = passedLat
    val selectedLng = passedLng


    var startHour by remember { mutableStateOf(8) }
    var endHour by remember { mutableStateOf(9) }

    val datePicker = remember {
        DatePickerDialog(context, { _, y, m, d ->
            calendar.set(y, m, d)
            dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    val timePicker = remember {
        TimePickerDialog(context, { _, h, m ->
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, m)
            timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Notification") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

            Text("Date: $dateText")
            Button(onClick = { datePicker.show() }) { Text("Pick Date") }

            Text("Time: $timeText")
            Button(onClick = { timePicker.show() }) { Text("Pick Time") }

            Text("Start: ${"%02d:00".format(startHour)}")
            Button(onClick = {
                TimePickerDialog(context, { _, hour, _ -> startHour = hour }, startHour, 0, true).show()
            }) { Text("Pick Start Hour") }

            Text("End: ${"%02d:00".format(endHour)}")
            Button(onClick = {
                TimePickerDialog(context, { _, hour, _ -> endHour = hour }, endHour, 0, true).show()
            }) { Text("Pick End Hour") }

            Button(onClick = { onLaunchMap() })
            {
                Text("Pick Location")
            }

            Text("Location: ${selectedLat ?: "-"}, ${selectedLng ?: "-"}")

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    onDone(
                        Notif(
                            title = title,
                            description = description,
                            timestamp = calendar.timeInMillis,
                            startHour = startHour,
                            endHour = endHour,
                            repeatIntervalMillis = null,
                            enabled = true,
                            latitude = selectedLat,
                            longitude = selectedLng
                        )
                    )
                }
            ) {
                Text("Create Alarm")
            }
        }
    }
}
