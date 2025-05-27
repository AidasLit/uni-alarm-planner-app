package com.example.labworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DayNotificationsFragment : Fragment() {

    companion object {
        private const val ARG_DATE = "date"

        fun newInstance(dateMillis: Long): DayNotificationsFragment {
            return DayNotificationsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATE, dateMillis)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val dateMillis = arguments?.getLong(ARG_DATE) ?: System.currentTimeMillis()
                DayNotificationsScreen(dateMillis = dateMillis)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DayNotificationsScreen(
        dateMillis: Long,
        viewModel: NotifViewModel = viewModel()
    ) {
        val coroutineScope = rememberCoroutineScope()
        var dayNotifs by remember { mutableStateOf<List<Notif>>(emptyList()) }

        LaunchedEffect(dateMillis) {
            coroutineScope.launch {
                val all = viewModel.getAllNotifs()

                val startOfDay = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val endOfDay = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis

                dayNotifs = all.filter { it.timestamp in startOfDay..endOfDay }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateMillis))
                        Text("Notifications for $dateText")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E)),
                )
            },
            containerColor = Color(0xFF121212)
        ) { padding ->
            if (dayNotifs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications for this day", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(dayNotifs) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(notif.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text(notif.description ?: "No description", color = Color.LightGray)
                                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(notif.timestamp))
                                Text("Time: $formattedTime", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
