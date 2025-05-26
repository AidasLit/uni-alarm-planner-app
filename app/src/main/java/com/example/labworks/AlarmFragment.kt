package com.example.labworks

import android.app.DatePickerDialog
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.TimePickerDialog
import java.util.Date



class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AlarmScreen()
            }
        }


    }

}

@Composable
fun AlarmScreen(viewModel: NotifViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var notifs by remember { mutableStateOf<List<Notif>>(emptyList()) }
    var selectedNotif by remember { mutableStateOf<Notif?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            notifs = viewModel.getAllNotifs()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AlarmHeader()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(notifs) { notif ->
                    var isChecked by remember { mutableStateOf(notif.enabled) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedNotif = notif },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = notif.title,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(notif.timestamp)),
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            Switch(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = it
                                    notif.enabled = it
                                    viewModel.addNotif(notif)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Green,
                                    uncheckedThumbColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }

            if (selectedNotif != null) {
                NotifDetailsDialog(
                    notif = selectedNotif!!,
                    onDismiss = { selectedNotif = null },
                    onDelete = {
                        coroutineScope.launch {
                            viewModel.deleteNotif(it)
                            notifs = viewModel.getAllNotifs()
                            selectedNotif = null
                        }
                    }
                )
            }
        }

        NotifButton(
            viewModel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            onNewNotif = {
                coroutineScope.launch {
                    notifs = viewModel.getAllNotifs()
                }
            }
        )
    }
}


@Composable
fun NotifDetailsDialog(
    notif: Notif,
    onDismiss: () -> Unit,
    onDelete: (Notif) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Alarm: ${notif.title}",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(notif.timestamp))}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                notif.description?.takeIf { it.isNotBlank() }?.let {
                    Text("Description: $it", fontSize = 16.sp, color = Color.LightGray)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { onDelete(notif) }) {
                        Text("Delete", color = Color.Red)
                    }
                }

            }
        }
    }
}





@Composable
fun AlarmHeader() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Alarm list",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ClockIcon,
                    contentDescription = "Alarm Icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun NotifButton(
    viewModel: NotifViewModel,
    modifier: Modifier,
    onNewNotif: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        NotifCreateDialog(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = { title, timestamp, description, startHour, endHour ->
                val newNotif = Notif(title, timestamp, description, startHour, endHour)
                viewModel.addNotif(newNotif)
                onNewNotif()
                showDialog.value = false
            }
        )
    }

    FloatingActionButton(
        onClick = { showDialog.value = true },
        containerColor = Color(0xFF333333),
        contentColor = Color.White,
        modifier = modifier
    ) {
        Icon(
            imageVector = PlusIcon,
            contentDescription = "Add an Alarm",
            tint = Color.White
        )
    }
}




@Composable
fun NotifCreateDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: (title: String, timestamp: Long, discription: String,startHour: Int, endHour: Int) -> Unit = { _, _, _, _, _ -> }
) {
    var newTitle by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendar = remember { Calendar.getInstance() }
    var dateText by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }
    var timeText by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)) }

    var startHour by remember { mutableStateOf(8) }
    var endHour by remember { mutableStateOf(9) }

    val startTimePicker = remember {
        TimePickerDialog(
            context,
            {_, hour, _ -> startHour = hour},
            startHour, 0, true
        )
    }

    val endTimePicker = remember {
        TimePickerDialog(
            context,
            {_, hour, _ -> endHour = hour},
            endHour, 0, true
        )
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Create a new Alarm", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    label = { Text("Enter Alarm title") }
                )

                var description by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Optional Description") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Selected date: $dateText", color = Color.Gray)
                Button(onClick = { datePickerDialog.show() }) {
                    Text("Pick Date")
                }

                Text("Selected time: $timeText", color = Color.Gray)
                Button(onClick = { timePickerDialog.show() }) {
                    Text("Pick Time")
                }

                Text("Start time: ${"%02d:00".format(startHour)}", color = Color.Gray)
                Button(onClick = { startTimePicker.show() }) {
                    Text("Pick Start Time")
                }

                Text("End time: ${"%02d:00".format(endHour)}", color = Color.Gray)
                Button(onClick = { endTimePicker.show() }) {
                    Text("Pick End Time")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismissRequest) { Text("Cancel") }
                    TextButton(onClick = {
                        onConfirmation(newTitle, calendar.timeInMillis, description, startHour, endHour)
                    }) {
                        Text("Add")
                    }
                }

            }
        }
    }
}





// Vektorinis „+“ ikonėlės aprašymas
val PlusIcon: ImageVector
    get() = Builder(
        name = "PlusIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
        ) {
            moveTo(10f, 4f)
            lineTo(14f, 4f)
            lineTo(14f, 10f)
            lineTo(20f, 10f)
            lineTo(20f, 14f)
            lineTo(14f, 14f)
            lineTo(14f, 20f)
            lineTo(10f, 20f)
            lineTo(10f, 14f)
            lineTo(4f, 14f)
            lineTo(4f, 10f)
            lineTo(10f, 10f)
            close()
        }
    }.build()

// Vektorinis laikrodžio ikonėlės aprašymas
val ClockIcon: ImageVector
    get() = Builder(
        name = "ClockIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
        ) {
            // Apskritimas
            moveTo(12f, 2f)
            arcToRelative(10f, 10f, 0f, true, true, 0.01f, 0f)
            close()
            // Rodyklės
            moveTo(11f, 6f)
            lineTo(11f, 13f)
            lineTo(16f, 16f)
            lineTo(16.5f, 15.3f)
            lineTo(12.5f, 13f)
            lineTo(12f, 6f)
            close()
        }
    }.build()
