package com.example.public_transport;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private Switch switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchNotifications = findViewById(R.id.switchNotifications);

        // TODO: Fetch current settings from Firebase
        // Example: set switch state based on user preference
        switchNotifications.setChecked(true); // Example static value

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Update settings in Firebase
            if (isChecked) {
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

