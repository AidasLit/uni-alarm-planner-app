package com.example.labworks

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import java.text.SimpleDateFormat
import java.util.*

class WeekCalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                WeekCalendarScreen()
            }
        }
    }

    @Composable
    fun WeekCalendarScreen() {
        val currentWeek = remember { mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }) }
        val viewModel: NotifViewModel = viewModel()
        var selectedNotif by remember { mutableStateOf<Notif?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(top = 24.dp)
        ) {
            WeekNavigationBar(currentCalendar = currentWeek)
            VerticalScrollGrid(
                startOfWeek = currentWeek.value,
                viewModel = viewModel,
                onNotifSelected = { selectedNotif = it }
            )
        }

        selectedNotif?.let { notif ->
            AlertDialog(
                onDismissRequest = { selectedNotif = null },
                confirmButton = {
                    TextButton(onClick = { selectedNotif = null }) {
                        Text("Close")
                    }
                },
                title = { Text("Alarm Info", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Title: ${notif.title}")
                        Text("Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(notif.timestamp))}")
                        Text("Enabled: ${notif.enabled}")
                    }
                },
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }

    @Composable
    fun WeekNavigationBar(currentCalendar: MutableState<Calendar>) {
        val weekStart = remember(currentCalendar.value) {
            (currentCalendar.value.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
        }
        val weekEnd = (weekStart.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 6) }
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                currentCalendar.value = (currentCalendar.value.clone() as Calendar).apply {
                    add(Calendar.WEEK_OF_YEAR, -1)
                }
            }) {
                Text("←")
            }

            Text(
                text = "Week of ${dateFormat.format(weekStart.time)} – ${dateFormat.format(weekEnd.time)}, ${yearFormat.format(weekStart.time)}",
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = {
                currentCalendar.value = (currentCalendar.value.clone() as Calendar).apply {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }) {
                Text("→")
            }
        }
    }

    @Composable
    fun VerticalScrollGrid(
        startOfWeek: Calendar,
        viewModel: NotifViewModel,
        onNotifSelected: (Notif) -> Unit
    ) {
        val today = Calendar.getInstance()
        val hours = 0..23

        val notifs by produceState(initialValue = emptyList<Notif>(), viewModel) {
            value = viewModel.getAllNotifs()
        }

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.width(40.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                hours.forEach { hour ->
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d:00", hour),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            repeat(7) { i ->
                val day = (startOfWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_WEEK, i) }
                val isToday = today.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)

                val formattedDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(day.time)
                val notifForDay = notifs.find {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp)) == formattedDay && it.enabled
                }

                val columnColor = when {
                    isToday -> Color(0xFF2E2E2E)
                    notifForDay != null -> Color(0xFF2A3A5E) //highlighted notif
                    else -> Color(0xFF1A1A1A)
                }

                Column(
                    modifier = Modifier
                        .width(53.dp)
                        .background(columnColor)
                        .clickable(enabled = notifForDay != null) {
                            notifForDay?.let { onNotifSelected(it) }
                        }
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("EEE\ndd", Locale.getDefault()).format(day.time),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    for (hour in hours) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color(0xFF292929))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
