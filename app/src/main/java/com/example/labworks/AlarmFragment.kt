package com.example.labworks

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Brush

class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                AlarmScreen()
            }
        }
    }
}

@Composable
fun AlarmScreen(
    viewModel: NotifViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AlarmHeader()
            // ... čia kiti dalykai, jei yra
        }

        // Apvalus + mygtukas apačioje (kaip buvo)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF333333), shape = CircleShape)
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .clickable {
                    val newNotif = Notif("temp")
                    viewModel.addNotif(newNotif)
                }
        ) {
            Icon(
                imageVector = PlusIcon,
                contentDescription = "Add Notif",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


@Composable
fun AlarmHeader() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add Alarm",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ClockIcon,
                    contentDescription = "Alarm Icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// Vektorinis „+“ ikonėlės aprašymas
val PlusIcon: ImageVector
    get() = Builder(
        name = "PlusIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
        ) {
            moveTo(10f, 4f)
            lineTo(14f, 4f)
            lineTo(14f, 10f)
            lineTo(20f, 10f)
            lineTo(20f, 14f)
            lineTo(14f, 14f)
            lineTo(14f, 20f)
            lineTo(10f, 20f)
            lineTo(10f, 14f)
            lineTo(4f, 14f)
            lineTo(4f, 10f)
            lineTo(10f, 10f)
            close()
        }
    }.build()

// Vektorinis laikrodžio ikonėlės aprašymas
val ClockIcon: ImageVector
    get() = Builder(
        name = "ClockIcon", defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
        ) {
            // Apskritimas
            moveTo(12f, 2f)
            arcToRelative(10f, 10f, 0f, true, true, 0.01f, 0f)
            close()
            // Rodyklės
            moveTo(11f, 6f)
            lineTo(11f, 13f)
            lineTo(16f, 16f)
            lineTo(16.5f, 15.3f)
            lineTo(12.5f, 13f)
            lineTo(12f, 6f)
            close()
        }
    }.build()
