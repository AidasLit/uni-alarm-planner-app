package com.example.labworks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tools.build.jetifier.core.utils.Log
import com.example.labworks.database.NotifViewModel

class CreateNotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: NotifViewModel = viewModel()
            val context = this

            // State to hold selected coordinates
            val selectedLat = remember { mutableStateOf<Double?>(null) }
            val selectedLng = remember { mutableStateOf<Double?>(null) }


            // Launcher to open MapSelectActivity and receive result
            val mapLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { data ->
                        val lat = data.getDoubleExtra("lat", Double.NaN)
                        val lng = data.getDoubleExtra("lng", Double.NaN)
                        if (!lat.isNaN() && !lng.isNaN()) {
                            selectedLat.value = lat
                            selectedLng.value = lng
                            Log.d("CreateNotif", "Received lat=$lat, lng=$lng")
                        }
                    }
                }
            }



            NotifCreateScreen(
                passedLat = selectedLat.value,
                passedLng = selectedLng.value,
                onDone = {
                    viewModel.addAndSchedule(context, it)
                    finish()
                },
                onBack = { finish() },
                onLaunchMap = {
                    val intent = Intent(context, MapSelectActivity::class.java)
                    mapLauncher.launch(intent)
                }
            )

        }
    }
}
