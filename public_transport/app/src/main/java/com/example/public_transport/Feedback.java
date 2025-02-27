package com.example.public_transport;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmitFeedback;

    private FirebaseDatabase database;
    private DatabaseReference feedbackRef;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile", null);

        if (mobile == null) {
            Toast.makeText(this, "Please log in to submit feedback.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PassengerLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_feedback);

        etFeedback = findViewById(R.id.etFeedback);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        database = FirebaseDatabase.getInstance();
        feedbackRef = database.getReference("/feedback");

        btnSubmitFeedback.setOnClickListener(v -> {
            String feedbackText = etFeedback.getText().toString().trim();
            if (!feedbackText.isEmpty()) {
                String feedbackId = feedbackRef.push().getKey();
                if (feedbackId != null) {
                    feedbackRef.child(feedbackId).child("mobile").setValue(mobile);
                    feedbackRef.child(feedbackId).child("feedback").setValue(feedbackText)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Feedback.this, "Feedback submitted. Thank you!", Toast.LENGTH_SHORT).show();
                                etFeedback.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Feedback.this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
