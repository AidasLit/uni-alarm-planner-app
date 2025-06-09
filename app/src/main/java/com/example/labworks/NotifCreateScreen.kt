package com.example.labworks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var dateText by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
    }
    var timeText by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time))
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

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Notification", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        bottomBar = {
            val isTitleValid = title.text.trim().isNotEmpty()

            val context = LocalContext.current

            Button(
                onClick = {
                    if (title.text.trim().isEmpty()) {
                        android.widget.Toast.makeText(context, "Please enter a title", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        onDone(
                            Notif(
                                title = title.text.trim(),
                                description = description.text.trim(),
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Create Alarm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

            StyledTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Title"
            )

            Spacer(modifier = Modifier.height(16.dp))

            StyledTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Description"
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoItem(label = "Date", value = dateText, onPick = { datePicker.show() })
            InfoItem(label = "Time", value = timeText, onPick = { timePicker.show() })

            // Start & End - disabled
//            InfoItem(
//                label = "Start",
//                value = "%02d:00".format(startHour),
//                disabled = true
//            )
//            InfoItem(
//                label = "End",
//                value = "%02d:00".format(endHour),
//                disabled = true
//            )

            val context = LocalContext.current
            val clipboardManager = context.getSystemService(android.content.ClipboardManager::class.java)

            InfoItem(
                label = "Location",
                value = if (selectedLat != null && selectedLng != null)
                    "Lat: %.5f\nLng: %.5f".format(selectedLat, selectedLng)
                else "-",
                onPick = { onLaunchMap() },
                onCopy = {
                    if (selectedLat != null && selectedLng != null) {
                        val coords = "Lat: %.5f, Lng: %.5f".format(selectedLat, selectedLng)
                        val clip = android.content.ClipData.newPlainText("Coordinates", coords)
                        clipboardManager.setPrimaryClip(clip)

                        // Show toast
                        android.widget.Toast.makeText(context, "Coordinates copied", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )




            if (selectedLat != null && selectedLng != null) {
                Spacer(modifier = Modifier.height(10.dp))

                AndroidView(
                    factory = { context ->
                        val mapView = com.google.android.gms.maps.MapView(context)
                        mapView.onCreate(null)
                        mapView.onResume()
                        mapView.getMapAsync { googleMap ->
                            val location = com.google.android.gms.maps.model.LatLng(selectedLat, selectedLng)
                            googleMap.uiSettings.setAllGesturesEnabled(false)
                            googleMap.addMarker(
                                com.google.android.gms.maps.model.MarkerOptions()
                                    .position(location)
                                    .title("Selected Location")
                            )
                            googleMap.moveCamera(
                                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(location, 14f)
                            )
                        }
                        mapView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

        }
    }

}

@Composable
fun StyledTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        if (value.text.isEmpty()) {
            Text(
                placeholder,
                color = Color.White,
                fontSize = 18.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onPick: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    disabled: Boolean = false
)
 {
    val fontSize = if (value == "-") 16.sp else 16.sp
    val backgroundColor = if (disabled) Color.Gray.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.2f)
    val textColor = if (disabled) Color.LightGray.copy(alpha = 0.5f) else Color.LightGray

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = if (disabled) "Coming soon" else value,
            color = textColor,
            fontSize = fontSize,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            softWrap = true,
            maxLines = 5 // or Int.MAX_VALUE if needed
        )
        if (!disabled && onCopy != null) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onCopy) {
                Text("Copy", color = Color.White, fontSize = 14.sp)
            }
        }
        if (!disabled && onPick != null) {
            Box(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            ) {
                TextButton(onClick = onPick) {
                    Text("Pick", color = Color.White, fontSize = 14.sp)
                }
            }
        }

    }
}
