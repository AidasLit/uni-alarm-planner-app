package com.example.labworks.settings;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labworks.R;

public class AppSettings extends AppCompatActivity {

    Button _button_back;
    Button _button_privacy;
    Button _button_about;
    Button _button_ring_duration;
    Button _button_alarm_sound;

    Dialog ringdurationdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //---------------Myktukai-------------------------------------------------------

        // Myktukas atidarantis pop up ring duration langelį

        ringdurationdialog = new Dialog(AppSettings.this);
        ringdurationdialog.setContentView(R.layout.ring_duration_screen);
        ringdurationdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        _button_ring_duration = findViewById(R.id.button6);
        _button_ring_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringdurationdialog.show();
            }
        });

        // Myktukas atidarantis alarm sound langa

        _button_about = findViewById(R.id.button7);
        _button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AppSettingsAlarmSound.class);
                startActivity(intent);
            }
        });

        // Myktukas atidarantis  Date and time langa

        _button_about = findViewById(R.id.button9);
        _button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AppSettingsDateTime.class);
                startActivity(intent);
            }
        });

        // Myktukas uždarantis dabartinį langą

        _button_back = findViewById(R.id.button3);
        _button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Myktukas atidarantis Privacy activity

        _button_privacy = findViewById(R.id.button4);
        _button_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AppSettingsPrivacy.class);
                startActivity(intent);
            }
        });

        // Myktukas atidarantis About activity

        _button_about = findViewById(R.id.button5);
        _button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AppSettingsAbout.class);
                startActivity(intent);
            }
        });

        //-----------------Myktukai-----------------------------------------------------


    }
}