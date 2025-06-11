package com.example.labworks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.draw.clipToBounds
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class WeekCalendarFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        CustomWeekCalendar(this@WeekCalendarFragment)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CustomWeekCalendar(fragment: Fragment) {
    val viewModel: NotifViewModel = viewModel()
    val today = Calendar.getInstance()

    var currentWeek by remember {
        mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) })
    }
    var weekOffset by remember { mutableStateOf(0) }
    var displayedTime by remember { mutableStateOf(currentWeek.timeInMillis) }

    // Delay before triggering animation
    LaunchedEffect(currentWeek.timeInMillis) {
        delay(32)
        displayedTime = currentWeek.timeInMillis
    }

    val hours = 0..23
    val notifColors = listOf(
        Color(0xFFB3D9FF), Color(0xFF80BFFF),
        Color(0xFF4DA6FF), Color(0xFF1A8CFF), Color(0xFF0066CC)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        val headerHeight = 60.dp
        val scrollAreaHeight = 1440.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Navigation bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ◀ Left Arrow Button
                Box(
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                ) {
                    TextButton(
                        onClick = {
                            currentWeek = (currentWeek.clone() as Calendar).apply {
                                add(Calendar.WEEK_OF_YEAR, -1)
                            }
                            weekOffset = -1
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.defaultMinSize(minWidth = 40.dp, minHeight = 36.dp)
                    ) {
                        Text("<", color = Color.White, fontSize = 24.sp, lineHeight = 24.sp)
                    }
                }

                // 📆 Week Range Label
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
                val weekStart = (currentWeek.clone() as Calendar).apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }
                val weekEnd = (weekStart.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 6) }

                Text(
                    text = "Week of ${dateFormat.format(weekStart.time)} – ${dateFormat.format(weekEnd.time)}, ${yearFormat.format(weekStart.time)}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                // ▶ Right Arrow Button
                Box(
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                ) {
                    TextButton(
                        onClick = {
                            currentWeek = (currentWeek.clone() as Calendar).apply {
                                add(Calendar.WEEK_OF_YEAR, 1)
                            }
                            weekOffset = 1
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.defaultMinSize(minWidth = 40.dp, minHeight = 36.dp)
                    ) {
                        Text(">", color = Color.White, fontSize = 24.sp, lineHeight = 24.sp)
                    }
                }
            }

            // Outer fixed scroll area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scrollAreaHeight) // same as column content height
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedContent(
                    targetState = displayedTime,
                    transitionSpec = {
                        if (weekOffset > 0) {
                            slideInHorizontally(tween(300)) { it } togetherWith slideOutHorizontally(tween(300)) { -it }
                        } else {
                            slideInHorizontally(tween(300)) { -it } togetherWith slideOutHorizontally(tween(300)) { it }
                        }.using(SizeTransform(clip = false) { _, _ -> tween(0) })
                    },
                    modifier = Modifier
                        .fillMaxSize()
                ) { weekMillis ->
                    val startOfWeek = Calendar.getInstance().apply {
                        timeInMillis = weekMillis
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    }

                    val notifs by produceState(initialValue = emptyList<Notif>(), viewModel) {
                        value = viewModel.getAllNotifs()
                    }

                    Row(Modifier.fillMaxSize()) {
                        // Time column
                        Column(Modifier.width(40.dp)) {
                            hours.forEach { hour ->
                                Box(
                                    modifier = Modifier.height(60.dp).fillMaxWidth(),
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

                        // 7 day columns
                        repeat(7) { i ->
                            val day = (startOfWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_WEEK, i) }
                            val isToday = today.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                                    today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)

                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(day.time)
                            val dayNotifs = notifs.filter {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp)) == formatted && it.enabled
                            }

                            val notifCount = dayNotifs.size
                            val dayColor = when {
                                !isToday && notifCount == 0 -> Color(0xFF1A1A1A)
                                isToday && notifCount == 0 -> Color(0xFF2E2E2E)
                                else -> notifColors[(notifCount - 1).coerceIn(0, notifColors.lastIndex)]
                            }

                            Column(
                                modifier = Modifier
                                    .width(53.dp)
                                    .then(if (isToday) Modifier.border(BorderStroke(2.dp, Color.Red))
                                    else Modifier.border(BorderStroke(0.5.dp, Color.DarkGray)))
                                    .background(dayColor)
                                    .padding(horizontal = 4.dp)
                                    .clickable(enabled = dayNotifs.isNotEmpty()) {
                                        val notif = dayNotifs.first()
                                        val dayFragment = DayNotificationsFragment.newInstance(notif.timestamp)
                                        fragment.parentFragmentManager.beginTransaction()
                                            .replace(R.id.fragment_container, dayFragment)
                                            .addToBackStack(null)
                                            .commit()
                                    }
                            ) {
                                Text(
                                    text = SimpleDateFormat("EEE\ndd", Locale.getDefault()).format(day.time),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                hours.forEach {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .background(dayColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


