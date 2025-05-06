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

        NotifButton(
            viewModel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        )
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
            Icons.Filled.Add,
            contentDescription = "Add a Ping",
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
                    text = "Create a new Ping",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    label = { Text("Enter Ping name") }
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