package com.example.public_transport;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PassengerLoginActivity extends AppCompatActivity {

    private EditText etMobile, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/passengers");

        etMobile = findViewById(R.id.etPassengerLoginMobile);
        etPassword = findViewById(R.id.etPassengerLoginPassword);
        btnLogin = findViewById(R.id.btnPassengerLogin);
        tvSignUp = findViewById(R.id.tvPassengerRegister);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (mobile.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            reference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String savedPassword = snapshot.child("password").getValue(String.class);
                        if (savedPassword != null && savedPassword.equals(password)) {
                            // Save login state
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("mobile", mobile);
                            editor.apply();

                            Toast.makeText(PassengerLoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PassengerLoginActivity.this, Main.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PassengerLoginActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PassengerLoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PassengerLoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, PassengerRegisterActivity.class);
            startActivity(intent);
        });
    }
}
