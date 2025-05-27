package com.example.labworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

class MonthCalendarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CustomMonthCalendar(this@MonthCalendarFragment) // pass the fragment
                    }
                }
            }
        }
    }
}


@Composable
fun CustomMonthCalendar(fragment: Fragment) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val viewModel: NotifViewModel = viewModel()
    var selectedNotif by remember { mutableStateOf<Notif?>(null) }

    var allNotifs by remember { mutableStateOf(emptyList<Notif>()) }
    LaunchedEffect(Unit) {
        allNotifs = viewModel.getAllNotifs()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val calendarHeight = screenHeight - 200.dp
    val totalRows = 6
    val cellHeight = calendarHeight / totalRows

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("lt"))
                    .replaceFirstChar { it.uppercaseChar() } + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            listOf("S", "P", "A", "T", "K", "Pn", "Š").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOffset = (currentMonth.atDay(1).dayOfWeek.value % 7)
        val dates = buildList<LocalDate?> {
            repeat(firstDayOffset) { add(null) }
            for (day in 1..currentMonth.lengthOfMonth()) {
                add(LocalDate.of(currentMonth.year, currentMonth.monthValue, day))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(cellHeight * totalRows)
        ) {
            items(dates.size) { index ->
                val date = dates[index]
                val isSelected = date == selectedDate
                val isToday = date == today

                // Find all notifs for that day
                val matchingNotifs = allNotifs.filter { notif ->
                    val notifDate = Instant.ofEpochMilli(notif.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    notif.enabled && notifDate == date
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cellHeight)
                        .border(BorderStroke(0.5.dp, Color.DarkGray))
                        .background(
                            when {
                                isSelected -> Color(0xFF3F51B5)
                                isToday -> Color(0xFF424242)
                                matchingNotifs.isNotEmpty() -> Color(0xFF2A3A5E)
                                else -> Color(0xFF1E1E1E)
                            }
                        )
                        .clickable(enabled = matchingNotifs.isNotEmpty()) {
                            date?.let {
                                val dayStartMillis = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                val dayFragment = DayNotificationsFragment.newInstance(dayStartMillis)
                                fragment.parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, dayFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date?.dayOfMonth?.toString() ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
