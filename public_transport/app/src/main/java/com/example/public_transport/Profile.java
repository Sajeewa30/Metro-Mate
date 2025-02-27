package com.example.public_transport;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfileNIC, tvProfileMobile;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile", null);

        if (mobile == null) {
            Toast.makeText(this, "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if not logged in
            return;
        }

        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileNIC = findViewById(R.id.tvProfileNIC);
        tvProfileMobile = findViewById(R.id.tvProfileMobile);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/passengers").child(mobile);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvProfileName.setText("Name: " + snapshot.child("name").getValue(String.class));
                    tvProfileEmail.setText("Email: " + snapshot.child("email").getValue(String.class));
                    tvProfileNIC.setText("NIC: " + snapshot.child("nic").getValue(String.class));
                    tvProfileMobile.setText("Mobile: " + snapshot.child("mobile").getValue(String.class));
                } else {
                    Toast.makeText(Profile.this, "Profile data not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to fetch profile data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
