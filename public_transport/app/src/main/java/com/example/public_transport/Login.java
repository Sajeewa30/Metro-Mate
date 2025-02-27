package com.example.public_transport;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText et_mobile;
    private EditText et_password;

    private String mobile="";
    private String password="";

    private Button btn_login;
    TextView tv_signup;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("/users");

        et_mobile=(EditText) findViewById(R.id.et_mobile_login);
        et_password=(EditText) findViewById(R.id.et_password_login);

        btn_login= (Button) findViewById(R.id.Button_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValues();
                authenticateUser();
            }
        });

        tv_signup=(TextView) findViewById(R.id.TextView_signup);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signup=new Intent(getApplicationContext(),Register.class);
                startActivity(intent_signup);
            }
        });

        et_mobile.setText("");
        et_password.setText("");

    }

    private void setValues() {
        mobile=et_mobile.getText().toString();
        password=et_password.getText().toString();
    }

    private void authenticateUser() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(mobile.equals("")||password.equals(""))){
                    if (dataSnapshot.hasChild(mobile)) {
                        String password_saved = dataSnapshot.child(mobile + "/password/").getValue().toString();
                        if (password.equals(password_saved)) {
                            Intent intent_map = new Intent(getApplicationContext(), Map.class);
                            intent_map.putExtra("mobile", mobile);
                            startActivity(intent_map);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Empty Username or Password", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}