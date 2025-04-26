package com.example.labworks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var ringtonePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ringtonePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                uri?.let {
                    val prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("alarmSound", it.toString()).apply()
                    Toast.makeText(requireContext(), "Sound selected", Toast.LENGTH_SHORT).show()
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
                SettingsScreen(ringtonePickerLauncher)
            }
        }
    }
}

@Composable
fun SettingsScreen(ringtonePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    var selectedCategory by remember { mutableStateOf("Time Format") }

    val categories = listOf("Time Format", "Ringtone", "Alarm ring duration", "About", "Privacy Policy")

    // Naujos spalvos
    val backgroundColor = Color(0xFF121212)           // Bendra fono spalva
    val leftPanelColor = Color(0xFF1A1A1A)             // Kairysis meniu - dar tamsesnis
    val rightPanelColor = Color(0xFF212121)            // Dešinysis turinio panelis
    val highlightColor = Color(0xFF1565C0)             // Mėlynas highlight, pakeista // pakeista
    val textColor = Color(0xFF1565C0)
    val secondaryTextColor = Color(0xFF1565C0)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor))
    {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF2C2C2C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Kairysis meniu
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(leftPanelColor) // pakeista
                    .padding(8.dp)
            ) {
                items(categories) { category ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                color = if (selectedCategory == category) highlightColor else Color.Transparent,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = category,
                            fontSize = 18.sp,
                            color = if (selectedCategory == category) Color.White else secondaryTextColor
                        )
                    }
                }
            }

            // Dešinysis panelis (turinys)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .background(rightPanelColor) // pakeista
                    .padding(16.dp)
            ) {
                when (selectedCategory) {
                    "Time Format" -> TimeFormatSetting(prefs, textColor)
                    "Ringtone" -> SoundPickerSetting(prefs, ringtonePickerLauncher, textColor)
                    "Alarm ring duration" -> AlarmDurationSetting(prefs, textColor)
                    "About" -> AboutSetting(textColor)
                    "Privacy Policy" -> PrivacyPolicySetting(textColor)
                }
            }
        }
    }
}

@Composable
fun TimeFormatSetting(prefs: android.content.SharedPreferences, textColor: Color) {
    var is24h by remember { mutableStateOf(prefs.getBoolean("use24Hour", true)) }

    Column {
        Text("Select Time Format:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = !is24h,
                onClick = {
                    is24h = false
                    prefs.edit().putBoolean("use24Hour", false).apply()
                },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
            )
            Text("12-hour format", modifier = Modifier.padding(start = 8.dp), color = textColor)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = is24h,
                onClick = {
                    is24h = true
                    prefs.edit().putBoolean("use24Hour", true).apply()
                },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Green)
            )
            Text("24-hour format", modifier = Modifier.padding(start = 8.dp), color = textColor)
        }
    }
}

@Composable
fun SoundPickerSetting(
    prefs: android.content.SharedPreferences,
    ringtonePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    textColor: Color
) {
    val context = LocalContext.current
    val savedUri = prefs.getString("alarmSound", null)?.let { Uri.parse(it) }
    var ringtoneTitle by remember {
        mutableStateOf(savedUri?.let { uri ->
            RingtoneManager.getRingtone(context, uri)?.getTitle(context)
        } ?: "No sound selected")
    }

    Column {
        Text("Select Ringtone:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            savedUri?.let {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it)
            }
            ringtonePickerLauncher.launch(intent)
        }) {
            Text("Pick Sound")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selected: $ringtoneTitle", color = textColor)
    }
}

@Composable
fun AlarmDurationSetting(prefs: android.content.SharedPreferences, textColor: Color) {
    val durations = listOf(5, 10, 15, 30, 60)
    val durationsLabels = listOf("5 seconds", "10 seconds", "15 seconds", "30 seconds", "1 minute")
    var selectedIndex by remember {
        mutableStateOf(durations.indexOf(prefs.getInt("alarmDuration", 10)))
    }

    Column {
        Text("Select Alarm Duration:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(durationsLabels[selectedIndex])
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                durationsLabels.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedIndex = index
                            prefs.edit().putInt("alarmDuration", durations[index]).apply()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AboutSetting(textColor: Color) {
    Text(
        text = "This app was created to demonstrate a settings screen with Jetpack Compose.\nVersion 1.0",
        fontSize = 16.sp,
        color = textColor
    )
}

@Composable
fun PrivacyPolicySetting(textColor: Color) {
    Text(
        text = "Privacy Policy: We do not collect any personal data.",
        fontSize = 16.sp,
        color = textColor
    )
}
