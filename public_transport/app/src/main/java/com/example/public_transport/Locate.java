package com.example.public_transport;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Locate extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private boolean setCamera = true;
    private boolean threadState = true;

    private LocationCallback mLocationCallback;
    private LatLng passengerLocation = new LatLng(6.898899, 79.860494);

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private Button btnSearchRoute;
    private EditText etRoute;
    private String route = "";

    private static final float NOTIFICATION_DISTANCE = 500; // Distance in meters to trigger notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/users");

        btnSearchRoute = findViewById(R.id.btn_search_route);
        etRoute = findViewById(R.id.et_route);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        new Thread(() -> {
            while (threadState) {
                locateBuses();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        btnSearchRoute.setOnClickListener(v -> {
            route = etRoute.getText().toString().trim();
            if (route.isEmpty()) {
                Toast.makeText(this, "Please enter a route number.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Searching buses on route: " + route, Toast.LENGTH_SHORT).show();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        passengerLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable location services.", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCamera = true;
        startPassengerLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadState = false;
    }

    private void startPassengerLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void locateBuses() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(passengerLocation)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.passenger)));

                if (setCamera) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(passengerLocation, 15));
                    setCamera = false;
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Safely retrieve data with type handling
                    String routeNo = postSnapshot.child("vehicle/route_no").getValue(String.class);
                    String registrationNo = postSnapshot.child("vehicle/registration_no").getValue(String.class);
                    Double lat = postSnapshot.child("location/latitude").getValue(Double.class);
                    Double lng = postSnapshot.child("location/longitude").getValue(Double.class);
                    Boolean visibility = postSnapshot.child("location/visibility").getValue(Boolean.class);

                    // Validate retrieved data
                    if (lat == null || lng == null || visibility == null) {
                        Log.e("Locate", "Invalid data found in Firebase");
                        continue;
                    }

                    // Only display markers for visible buses matching the route
                    if (visibility && (route.isEmpty() || route.equals(routeNo))) {
                        LatLng busLocation = new LatLng(lat, lng);
                        mMap.addMarker(new MarkerOptions()
                                .position(busLocation)
                                .title(routeNo + " | " + registrationNo)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Locate.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String calculateEstimatedTime(float distance) {
        float averageSpeedMetersPerSecond = 10; // Average bus speed
        int estimatedTimeSeconds = (int) (distance / averageSpeedMetersPerSecond);
        int minutes = estimatedTimeSeconds / 60;
        int seconds = estimatedTimeSeconds % 60;

        return minutes + "m " + seconds + "s";
    }

    private void sendNotification(String title, String message) {
        String channelId = "bus_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Bus Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
