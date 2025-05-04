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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity


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






@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(ringtonePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>) {

    val categories =
        listOf("Time Format", "Ringtone", "Alarm ring duration", "About", "Privacy Policy")

    var selectedCategory by remember { mutableStateOf("Time Format") }
    var previousCategory by remember { mutableStateOf("Time Format") }

    val currentIndex = categories.indexOf(selectedCategory)
    val previousIndex = categories.indexOf(previousCategory)


    val context = LocalContext.current
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)


    // Naujos spalvos
    val backgroundColor = Color(0xFF121212)           // Bendra fono spalva
    val leftPanelColor = Color(0xFF1A1A1A)             // Kairysis meniu - dar tamsesnis
    val rightPanelColor = Color(0xFF212121)            // Dešinysis turinio panelis
    val highlightColor = Color(0xFF1565C0)             // Mėlynas highlight, pakeista // pakeista
    val textColor = Color(0xFF1565C0)
    val secondaryTextColor = Color(0xFF1565C0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ✅ Top bar (fixed height)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // this ensures the box fills remaining space below top bar
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Left-side menu
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(leftPanelColor)
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
                                .clickable {
                                    if (category != selectedCategory) {
                                        previousCategory = selectedCategory
                                        selectedCategory = category
                                    }
                                }
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

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .background(rightPanelColor)
                        .padding(16.dp)
                        .clipToBounds() // This ensures the animation doesn't escape its box
                ) {
                    AnimatedContent(
                        targetState = selectedCategory,
                        transitionSpec = {
                            val targetIndex = categories.indexOf(targetState)
                            val initialIndex = categories.indexOf(initialState)

                            if (targetIndex > initialIndex) {
                                slideInVertically { it } + fadeIn() with slideOutVertically { -it } + fadeOut()
                            } else {
                                slideInVertically { -it } + fadeIn() with slideOutVertically { it } + fadeOut()
                            }.using(SizeTransform(clip = false))
                },
                        modifier = Modifier.fillMaxSize()
                    ) { category ->
                        when (category) {
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
    val context = LocalContext.current
    val aboutText = readRawTextFile(context, R.raw.about)

    Text(
        text = aboutText,
        fontSize = 16.sp,
        color = textColor
    )
}

@Composable
fun PrivacyPolicySetting(textColor: Color) {
    val context = LocalContext.current
    val privacyText = readRawTextFile(context, R.raw.privacy)
    Text(
        text = privacyText,
        fontSize = 16.sp,
        color = textColor
    )
}

@Composable
fun readRawTextFile(context: Context, rawResId: Int): String {
    return remember(rawResId) {
        context.resources.openRawResource(rawResId).bufferedReader().use { it.readText() }
    }
}
