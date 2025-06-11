package com.example.labworks

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditNotificationFragment : Fragment() {

    companion object {
        private const val ARG_ID = "notif_id"
        fun newInstance(id: Long): EditNotificationFragment {
            val fragment = EditNotificationFragment()
            val args = Bundle()
            args.putLong(ARG_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val id = requireArguments().getLong(ARG_ID)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        EditNotificationScreen(id)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNotificationScreen(
    id: Long,
    viewModel: NotifViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notif by remember { mutableStateOf<Notif?>(null) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    val calendar = remember { Calendar.getInstance() }
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val mapLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lat = result.data?.getDoubleExtra("lat", 0.0)
            val lng = result.data?.getDoubleExtra("lng", 0.0)
            Log.d("EditNotificationScreen", "Received from map: $lat, $lng")
            if (lat != null && lng != null) {
                selectedLat = lat
                selectedLng = lng
            }
        }
    }

    LaunchedEffect(id) {
        val loaded = viewModel.getNotifById(id)
        if (loaded != null) {
            notif = loaded
            title = TextFieldValue(loaded.title)
            description = TextFieldValue(loaded.description ?: "")
            calendar.timeInMillis = loaded.timestamp
            selectedLat = loaded.latitude
            selectedLng = loaded.longitude
            dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }
    }

    if (notif == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val scrollState = rememberScrollState()

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                calendar.set(y, m, d)
                dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, h, m ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this notification? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    notif?.let {
                        scope.launch {
                            viewModel.deleteNotif(it)
                            (context as? FragmentActivity)?.supportFragmentManager?.popBackStack()
                        }
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Notification", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? FragmentActivity)?.supportFragmentManager?.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete Notification", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        notif?.let { current ->
                            val updated = current.copy(
                                title = title.text.trim(),
                                description = description.text.trim(),
                                timestamp = calendar.timeInMillis,
                                latitude = selectedLat,
                                longitude = selectedLng
                            )
                            scope.launch {
                                viewModel.addNotif(updated)
                                (context as? FragmentActivity)?.supportFragmentManager?.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoItem("Date", dateText, onPick = { datePicker.show() })
            InfoItem("Time", timeText, onPick = { timePicker.show() })

            InfoItem(
                label = "Location",
                value = if (selectedLat != null && selectedLng != null)
                    "Lat: %.5f\nLng: %.5f".format(selectedLat, selectedLng)
                else "-",
                onPick = {
                    val intent = Intent(context, MapSelectActivity::class.java)
                    intent.putExtra("lat", selectedLat ?: 0.0)
                    intent.putExtra("lng", selectedLng ?: 0.0)
                    mapLauncher.launch(intent)
                },
                onCopy = {
                    if (selectedLat != null && selectedLng != null) {
                        val coords = "Lat: %.5f, Lng: %.5f".format(selectedLat, selectedLng)
                        val clip = android.content.ClipData.newPlainText("Coordinates", coords)
                        val clipboardManager = context.getSystemService(android.content.ClipboardManager::class.java)
                        clipboardManager.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "Coordinates copied", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )

            if (selectedLat != null && selectedLng != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AndroidView(factory = { context ->
                    val mapView = com.google.android.gms.maps.MapView(context)
                    mapView.onCreate(null)
                    mapView.onResume()
                    mapView.getMapAsync { map ->
                        val latLng = com.google.android.gms.maps.model.LatLng(selectedLat!!, selectedLng!!)
                        map.uiSettings.setAllGesturesEnabled(false)
                        map.addMarker(com.google.android.gms.maps.model.MarkerOptions().position(latLng).title("Selected Location"))
                        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                    }
                    mapView
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 8.dp))
            }
        }
    }
}
