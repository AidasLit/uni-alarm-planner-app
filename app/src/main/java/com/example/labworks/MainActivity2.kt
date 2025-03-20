package com.example.labworks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.labworks.database.NotifViewModel
import com.example.labworks.database.data.Notif

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button : Button = findViewById(R.id.button2)
        //val databaseViewModel : NotifViewModel = ViewModelProvider(this)[NotifViewModel::class]
        val databaseViewModel by viewModels<NotifViewModel>()

        val notifDisplay : TextView = findViewById(R.id.textView10)
        notifDisplay.text = databaseViewModel.myNotif.title

        button.setOnClickListener{
            val tempNotif : Notif = Notif("my Notif yippie")
            tempNotif.id = 1
            databaseViewModel.addNotif(tempNotif)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}