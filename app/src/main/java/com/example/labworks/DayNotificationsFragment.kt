package com.example.labworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val initialDateMillis = arguments?.getLong(ARG_DATE) ?: System.currentTimeMillis()
                        DayNotificationsScreen(initialDateMillis)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayNotificationsScreen(
    initialDateMillis: Long,
    viewModel: NotifViewModel = viewModel()
) {
    val context = LocalContext.current
    val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
    val coroutineScope = rememberCoroutineScope()

    var dateMillis by remember { mutableStateOf(initialDateMillis) }
    var allNotifs by remember { mutableStateOf(emptyList<Notif>()) }
    var dayNotifs by remember { mutableStateOf<List<Notif>>(emptyList()) }

    LaunchedEffect(Unit) {
        allNotifs = viewModel.getAllNotifs()
    }

    LaunchedEffect(dateMillis, allNotifs) {
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

        dayNotifs = allNotifs.filter { it.timestamp in startOfDay..endOfDay }
    }

    val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateMillis))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$dateText", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        fragmentManager?.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(dateMillis) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount > 0) {
                            dateMillis -= 86400000 // Previous day
                        } else if (dragAmount < 0) {
                            dateMillis += 86400000 // Next day
                        }
                    }
                }
        ) {
            if (dayNotifs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications for this day", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(dayNotifs) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(notif.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp), color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(notif.description ?: "No description", color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(notif.timestamp))
                                Text("Time: $formattedTime", color = Color.White)

                                if (notif.latitude != null && notif.longitude != null) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    AndroidView(factory = { ctx ->
                                        val mapView = MapView(ctx).apply {
                                            onCreate(null)
                                            onResume()
                                            getMapAsync { map ->
                                                val latLng = LatLng(notif.latitude, notif.longitude)
                                                map.uiSettings.setAllGesturesEnabled(false)
                                                map.addMarker(MarkerOptions().position(latLng).title("Location"))
                                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                                            }
                                        }
                                        mapView
                                    }, modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp))

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            fragmentManager?.beginTransaction()
                                                ?.replace(
                                                    android.R.id.content,
                                                    MapFragment.newInstance(notif.latitude, notif.longitude)
                                                )
                                                ?.addToBackStack(null)
                                                ?.commit()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("View Full Map")
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
