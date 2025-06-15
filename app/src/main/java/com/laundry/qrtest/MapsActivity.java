package com.laundry.qrtest;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.laundry.qrtest.databinding.ActivityMapsBinding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_REQUEST_CODE = 1000;

    Button btnset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        btnset = findViewById(R.id.btnset);

        // Inisialisasi lokasi client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Load Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Meminta izin lokasi jika belum diberikan
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // Menampilkan lokasi pengguna
        mMap.setMyLocationEnabled(true);

        // Mengambil lokasi terakhir
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Lokasi Anda"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        });

        // Event klik pada map untuk mendapatkan koordinat
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Koordinat: " + latLng.latitude + ", " + latLng.longitude));
        });

        // Event saat peta digeser
        mMap.setOnCameraIdleListener(() -> {
            LatLng centerLatLng = mMap.getCameraPosition().target;
            updateLatLng(centerLatLng);
        });
    }
    private void updateLatLng(LatLng latLng) {
//        txtLatLong.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = latLng.latitude+","+latLng.longitude;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT_TEXT", text);
                setResult(RESULT_OK, resultIntent); // Kirim hasil balik ke MainActivity
                finish(); // Tutup activity
            }
        });

    }

    // Handle hasil request izin lokasi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate(); // Restart Activity agar izin diterapkan
            }
        }
    }
}