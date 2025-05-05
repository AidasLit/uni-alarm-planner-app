package com.example.labworks

import android.app.Dialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
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
import androidx.compose.ui.window.Dialog

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
            .background(Color(0xFF121212)) // Tamsus fonas
    ) {
        // Tekstas "Add Alarm" viršuje
        Text(
            text = "Add Alarm",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        // Apvalus + mygtukas apačioje
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF333333), shape = CircleShape)
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .clickable {
                    // TODO: Čia įdėsi, ką veiks mygtukas

//                    NotifCreateDialog(
//                        {},
//                        {}
//                    )

                    val newNotif = Notif("temp")

                    viewModel.addNotif(newNotif)

                    println(newNotif.id)

                    /*viewModel.addDescriptionComponent(
                        newNotif,
                        title = "test component title",
                        description = "test description"
                    )*/
                }
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NotifCreateDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "This is a dialog with buttons and an image.",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}