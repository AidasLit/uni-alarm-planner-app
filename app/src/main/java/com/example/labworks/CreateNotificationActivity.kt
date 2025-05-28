package com.example.labworks

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labworks.database.NotifViewModel

class CreateNotificationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: NotifViewModel = viewModel()
            val context = this

            NotifCreateScreen(
                onDone = {
                    viewModel.addAndSchedule(context, it)
                    finish()
                },
                onBack = { finish() },
                onLaunchMap = {
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, MapFragment())
                        .addToBackStack(null)
                        .commit()
                }
            )
        }

    }
}
