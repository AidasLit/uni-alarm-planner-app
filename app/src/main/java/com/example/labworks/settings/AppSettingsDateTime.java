package com.example.labworks.settings;

import android.app.Dialog;
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

public class AppSettingsDateTime extends AppCompatActivity {

    Button _button_back;

    Dialog timeformatdialog;

    Button _button_time_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_settings_date_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Myktukas uždarantis dabartinį langą

        _button_back = findViewById(R.id.button14);
        _button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        timeformatdialog = new Dialog(AppSettingsDateTime.this);
        timeformatdialog.setContentView(R.layout.time_format_screen_box);
        timeformatdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        _button_time_format = findViewById(R.id.button15);
        _button_time_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeformatdialog.show();
            }
        });
    }
}