package com.example.public_transport;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PassengerRegisterActivity extends AppCompatActivity {
    private EditText etName, etNIC, etEmail, etMobile, etPassword;
    private Button btnRegister;
    private CheckBox checkBoxTerms;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_register);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/passengers");

        // Initialize UI components
        etName = findViewById(R.id.etPassengerName);
        etNIC = findViewById(R.id.etPassengerNIC);
        etEmail = findViewById(R.id.etPassengerEmail);
        etMobile = findViewById(R.id.etPassengerMobile);
        etPassword = findViewById(R.id.etPassengerPassword);
        btnRegister = findViewById(R.id.btnRegisterPassenger);
        checkBoxTerms = findViewById(R.id.checkBoxTermsAndConditions);

        // Register button logic
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String nic = etNIC.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || nic.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                Toast.makeText(PassengerRegisterActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
            } else if (!checkBoxTerms.isChecked()) {
                Toast.makeText(PassengerRegisterActivity.this, "Please accept the Terms and Conditions.", Toast.LENGTH_LONG).show();
            } else {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(mobile)) {
                            Toast.makeText(PassengerRegisterActivity.this, "Passenger already registered.", Toast.LENGTH_LONG).show();
                        } else {
                            DatabaseReference userRef = reference.child(mobile);
                            userRef.child("name").setValue(name);
                            userRef.child("nic").setValue(nic);
                            userRef.child("email").setValue(email);
                            userRef.child("password").setValue(password);
                            userRef.child("mobile").setValue(mobile);

                            Toast.makeText(PassengerRegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();

                            // Redirect to login
                            Intent intent = new Intent(PassengerRegisterActivity.this, PassengerLoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PassengerRegisterActivity.this, "Registration failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
