package com.example.labworks

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

//            val coroutineScope = rememberCoroutineScope()
//            var allNotifs = remember { mutableStateOf<List<Notif>?>(null) }
//
//            coroutineScope.launch() {
//                allNotifs = viewModel.getAllNotifs()
//            }
//
//            allNotifs.value?.let {
//                for (i in 0 until allNotifs.value.size){
//                    print(notif.title)
//                }
//            }


        }

        NotifButton(
            viewModel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        )
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
                text = "Alarm list",
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

@Composable
fun NotifButton(viewModel: NotifViewModel, modifier: Modifier){
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        NotifCreateDialog(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = { title ->
                val newNotif = Notif(title)
                viewModel.addNotif(newNotif)

                showDialog.value = false
            }
        )
    }

    FloatingActionButton(
        onClick = { showDialog.value = true },
        containerColor = Color(0xFF333333),
        contentColor = Color.White,
        modifier = modifier
    ) {
        Icon(
            imageVector = PlusIcon,
            contentDescription = "Add an Alarm",
            tint = Color.White
        )
    }
}




@Composable
fun NotifCreateDialog(
    onDismissRequest: () -> Unit = {},
    onConfirmation: (title : String) -> Unit = {}
) {
    var newTitle by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Create a new Alarm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    label = { Text("Enter Alarm title") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onConfirmation(newTitle) },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text("Add")
                    }
                }
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
