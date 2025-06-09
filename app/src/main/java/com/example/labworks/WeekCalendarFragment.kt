package com.example.labworks

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
                onNotifSelected = { notif ->
                    val fragment = DayNotificationsFragment.newInstance(notif.timestamp)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment) // Make sure you have a FrameLayout with this ID
                        .addToBackStack(null)
                        .commit()
                }

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
                        Text("Start Hour: ${notif.startHour}:00")
                        Text("End Hour: ${notif.endHour}:00")
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
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            ) {
                TextButton(
                    onClick = {
                        currentCalendar.value = (currentCalendar.value.clone() as Calendar).apply {
                            add(Calendar.WEEK_OF_YEAR, -1)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 36.dp, minHeight = 36.dp)
                        .height(36.dp)
                ) {
                    Text(
                        text = "<",
                        color = Color.White,
                        fontSize = 28.sp,         // Large enough
                        lineHeight = 28.sp,       // Matches font size to avoid clipping
                        modifier = Modifier
                            .padding(bottom = 2.dp) // Tiny bottom margin to avoid visual clipping
                    )
                }


            }
            Text(
                text = "Week of ${dateFormat.format(weekStart.time)} – ${dateFormat.format(weekEnd.time)}, ${yearFormat.format(weekStart.time)}",
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            ) {
                TextButton(
                    onClick = {
                        currentCalendar.value = (currentCalendar.value.clone() as Calendar).apply {
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 36.dp, minHeight = 36.dp)
                        .height(36.dp)
                ) {
                    Text(
                        text = ">",
                        color = Color.White,
                        fontSize = 28.sp,         // Large enough
                        lineHeight = 28.sp,       // Matches font size to avoid clipping
                        modifier = Modifier
                            .padding(bottom = 2.dp) // Tiny bottom margin to avoid visual clipping
                    )
                }

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

                val dayNotifs = notifs.filter {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp)) == formattedDay && it.enabled
                }

                val notifColors = listOf(
                    Color(0xFFB3D9FF), // Very Light Blue (1 notif)
                    Color(0xFF80BFFF), // Light Blue (2 notifs)
                    Color(0xFF4DA6FF), // Medium Blue (3 notifs)
                    Color(0xFF1A8CFF), // Darker Blue (4 notifs)
                    Color(0xFF0066CC)  // Very Dark Blue (5+ notifs)
                )


                val notifCount = dayNotifs.size

                val hasNotif = notifCount > 0
                val dayColor = when {
                    !hasNotif && isToday -> Color(0xFF2E2E2E)
                    !hasNotif -> Color(0xFF1A1A1A)
                    else -> {
                        val colorIndex = (notifCount - 1).coerceIn(0, notifColors.lastIndex)
                        notifColors[colorIndex]
                    }
                }


                Column(
                    modifier = Modifier
                        .width(53.dp)
                        .then(
                            if (isToday) Modifier.border(BorderStroke(2.dp, Color.Red))
                            else Modifier.border(BorderStroke(0.5.dp, Color.DarkGray))
                        )
                        .background(dayColor)
                        .clickable(enabled = hasNotif) {
                            dayNotifs.firstOrNull()?.let { onNotifSelected(it) }
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
                                .background(dayColor)
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

fun interpolateColor(from: Color, to: Color, fraction: Float): Color {
    return Color(
        red = from.red + (to.red - from.red) * fraction,
        green = from.green + (to.green - from.green) * fraction,
        blue = from.blue + (to.blue - from.blue) * fraction,
        alpha = 1f
    )
}


