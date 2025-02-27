package com.example.public_transport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Button btnLocateBuses;
    private TextView tvDriverInterface, tvPassengerLogin, tvPassengerRegister;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up Navigation Drawer Header with user details
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = headerView.findViewById(R.id.textViewUserEmail);
        ImageView imageViewProfile = headerView.findViewById(R.id.imageViewProfile);

        // Set dynamic user details if logged in
        if (isUserLoggedIn()) {
            String userName = sharedPreferences.getString("userName", "Welcome!");
            String userEmail = sharedPreferences.getString("userEmail", "user@example.com");

            textViewUserName.setText(userName);
            textViewUserEmail.setText(userEmail);

            // Optional: Set profile picture if available
            imageViewProfile.setImageResource(R.drawable.ic_profile); // Replace with dynamic logic if needed
        }

        // Setup ActionBarDrawerToggle to sync the state of the drawer with the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set NavigationItemSelectedListener to handle menu item clicks
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize UI Components
        btnLocateBuses = findViewById(R.id.btn_locate_busses);
        tvDriverInterface = findViewById(R.id.tv_driver_interface);
        tvPassengerLogin = findViewById(R.id.tv_passenger_login);
        tvPassengerRegister = findViewById(R.id.tv_passenger_register);

        // Set up OnClickListeners
        btnLocateBuses.setOnClickListener(view -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(Main.this, Locate.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            }
        });

        tvDriverInterface.setOnClickListener(view -> {
            Intent intent = new Intent(Main.this, Login.class);
            startActivity(intent);
        });

        tvPassengerLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Main.this, PassengerLoginActivity.class);
            startActivity(intent);
        });

        tvPassengerRegister.setOnClickListener(view -> {
            Intent intent = new Intent(Main.this, PassengerRegisterActivity.class);
            startActivity(intent);
        });

        // Highlight Home as the selected item
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            if (isUserLoggedIn()) {
                Intent profileIntent = new Intent(Main.this, Profile.class);
                profileIntent.putExtra("mobile", getCurrentUserMobile());
                startActivity(profileIntent);
            } else {
                Toast.makeText(this, "Please log in to view your profile!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(Main.this, Settings.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_feedback) {
            if (isUserLoggedIn()) {
                Intent feedbackIntent = new Intent(Main.this, Feedback.class);
                startActivity(feedbackIntent);
            } else {
                Toast.makeText(this, "Please log in to provide feedback!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_route_table) {
            // Open the Route Table activity
            Intent routeTableIntent = new Intent(Main.this, RouteTableActivity.class);
            startActivity(routeTableIntent);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private boolean isUserLoggedIn() {
        return sharedPreferences.getString("mobile", null) != null;
    }

    private String getCurrentUserMobile() {
        return sharedPreferences.getString("mobile", null);
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PassengerLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
