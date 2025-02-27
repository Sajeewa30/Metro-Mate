package com.example.public_transport;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteTableActivity extends AppCompatActivity {

    private ListView listViewRoutes;
    private EditText searchDestination;

    private HashMap<String, String> routeTable; // Stores destination and corresponding route number
    private List<String> displayedRoutes; // List of displayed routes
    private RouteAdapter routeAdapter; // Custom adapter for the ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_table);

        listViewRoutes = findViewById(R.id.listViewRoutes);
        searchDestination = findViewById(R.id.searchDestination);

        // Initialize the route table (replace with real data)
        routeTable = new HashMap<>();
        routeTable.put("City Center", "Route 1");
        routeTable.put("Airport", "Route 2");
        routeTable.put("Train Station", "Route 3");
        routeTable.put("University", "Route 4");
        routeTable.put("Mall", "Route 5");

        // Initialize the displayed routes list
        displayedRoutes = new ArrayList<>(routeTable.keySet());

        // Set up the custom adapter
        routeAdapter = new RouteAdapter(this, displayedRoutes, routeTable);
        listViewRoutes.setAdapter(routeAdapter);

        // Search functionality
        searchDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRoutes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle item click
        listViewRoutes.setOnItemClickListener((parent, view, position, id) -> {
            String destination = displayedRoutes.get(position);
            String route = routeTable.get(destination);
            Toast.makeText(this, "Route for " + destination + ": " + route, Toast.LENGTH_SHORT).show();
        });
    }

    private void filterRoutes(String query) {
        displayedRoutes.clear();
        for (String destination : routeTable.keySet()) {
            if (destination.toLowerCase().contains(query.toLowerCase())) {
                displayedRoutes.add(destination);
            }
        }
        routeAdapter.notifyDataSetChanged();
    }
}
