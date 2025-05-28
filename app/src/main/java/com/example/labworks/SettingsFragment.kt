package com.example.labworks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource


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
        listOf("Time Format", "Ringtone", "Vibration Strength", "Alarm ring duration", "About", "Privacy Policy")

    var selectedCategory by remember { mutableStateOf("Time Format") }
    var previousCategory by remember { mutableStateOf("Time Format") }

    val currentIndex = categories.indexOf(selectedCategory)
    val previousIndex = categories.indexOf(previousCategory)

    var ringtoneSignal by remember { mutableStateOf(0) }


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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.wrench_icon),
                    contentDescription = "Wrench",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
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
                        val isSelected = selectedCategory == category
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            animationSpec = tween(durationMillis = 200),
                            label = "scaleAnimation"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .background(
                                    color = if (isSelected) highlightColor else Color.Transparent,
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
                                color = if (isSelected) Color.White else secondaryTextColor
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
                            "Ringtone" -> SoundPickerSetting(prefs, ringtonePickerLauncher, textColor, ringtoneSignal)
                            "Vibration Strength" -> VibrationStrengthSetting(prefs, textColor)
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
    prefs: SharedPreferences,
    ringtonePickerLauncher: ActivityResultLauncher<Intent>,
    textColor: Color,
    ringtoneSignal: Int
) {
    val context = LocalContext.current
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var ringtoneTitle by remember { mutableStateOf("No sound selected") }
    var isPlaying by remember { mutableStateOf(false) }
    var ringtone by remember { mutableStateOf<android.media.Ringtone?>(null) }

    // Update on signal change
    LaunchedEffect(ringtoneSignal) {
        val savedUri = prefs.getString("alarmSound", null)?.let { Uri.parse(it) }
        currentUri = savedUri
        ringtoneTitle = savedUri?.let {
            RingtoneManager.getRingtone(context, it)?.getTitle(context)
        } ?: "No sound selected"
    }

    Column {
        Text("Select Ringtone:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                    currentUri?.let {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it)
                    }
                }
                ringtonePickerLauncher.launch(intent)
            }) {
                Text("Pick Sound")
            }

            Spacer(modifier = Modifier.width(16.dp))



            // Color animation
            val buttonColor by animateColorAsState(
                targetValue = if (isPlaying) Color.Red else MaterialTheme.colorScheme.primary,
                animationSpec = tween(300),
                label = "buttonColor"
            )

// Rotation animation
            val infiniteTransition = rememberInfiniteTransition(label = "vibration")
            val rotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rotationAnim"
            )

            Button(
                onClick = {
                    if (isPlaying) {
                        ringtone?.stop()
                        isPlaying = false
                    } else {
                        currentUri?.let { uri ->
                            ringtone = RingtoneManager.getRingtone(context, uri)
                            ringtone?.play()
                            isPlaying = true
                        }
                    }
                },
                enabled = currentUri != null,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = if (isPlaying) rotation else 0f
                    }
            ) {
                Text(if (isPlaying) "Stop" else "Play")
            }




        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selected: $ringtoneTitle", color = textColor)
    }
}





@Composable
fun AlarmDurationSetting(prefs: SharedPreferences, textColor: Color) {
    val durations = listOf(5, 10, 15, 30, 60)
    val durationsLabels = listOf("5 seconds", "10 seconds", "15 seconds", "30 seconds", "1 minute")
    var selectedIndex by remember {
        mutableStateOf(durations.indexOf(prefs.getInt("alarmDuration", 10)))
    }

    var expanded by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()  // ✅ Needed to launch delay

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "buttonScale"
    )

    Column {
        Text(
            "Select Alarm Duration:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            OutlinedButton(
                onClick = {
                    pressed = true
                    expanded = true
                    scope.launch {
                        kotlinx.coroutines.delay(150)
                        pressed = false
                    }
                }
            ) {
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
fun VibrationStrengthSetting(prefs: SharedPreferences, textColor: Color) {
    val strengths = listOf("Off", "Low", "Medium", "Strong")
    val values = listOf(0L, 100L, 300L, 600L) // durations
    var selectedIndex by remember {
        mutableStateOf(values.indexOf(prefs.getLong("vibrationStrength", 300L)))
    }

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Select Vibration Strength:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(strengths[selectedIndex])
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                strengths.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedIndex = index
                            prefs.edit().putLong("vibrationStrength", values[index]).apply()
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon), // <- tavo ikona iš drawable
            contentDescription = "App Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "PingMe App",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version 1.0",
            fontSize = 16.sp,
            color = textColor
        )
    }
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
