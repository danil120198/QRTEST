package com.laundry.qrtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.laundry.qrtest.Petugas.HomePetugas;

import java.util.Map;

import android.Manifest;
public class Dashboard extends AppCompatActivity {

    private ActivityResultLauncher<String> requestSinglePermissionLauncher;
    private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Inisialisasi permission launcher
        initializePermissionLaunchers();

        // Mulai meminta izin satu per satu
        requestPermissionsSequentially();

        SharedPrefManager sharedPrefManager = new SharedPrefManager(Dashboard.this);

        if (sharedPrefManager.getSPSudahLogin()){
            if (sharedPrefManager.getStatus().equals("user")){
                Intent intent = new Intent(Dashboard.this, HomeUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(Dashboard.this, HomePetugas.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }
        // Redirect ke halaman Home sesuai role default


        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });



        // Inisialisasi permission launcher
        initializePermissionLaunchers();

        // Mulai meminta izin satu per satu
        requestPermissionsSequentially();

        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Izin diberikan
//                                showPermissionGrantedMessage();
                    } else {
                        // Izin ditolak
//                                showPermissionDeniedMessage();
                    }
                });

        // Minta izin
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

    }
    private void initializePermissionLaunchers() {
        // Launcher untuk izin tunggal
        requestSinglePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Melanjutkan ke permintaan izin berikutnya
                        requestNextPermission();
                    } else {
//                        Toast.makeText(this, "Izin ditolak, beberapa fitur mungkin tidak berfungsi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int permissionIndex = 0;
    private final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    // Mulai meminta izin dari awal
    private void requestPermissionsSequentially() {
        permissionIndex = 0; // Mulai dari izin pertama
        requestNextPermission();
    }

    // Meminta izin berikutnya dalam daftar
    private void requestNextPermission() {
        if (permissionIndex < permissions.length) {
            String currentPermission = permissions[permissionIndex];
            if (ContextCompat.checkSelfPermission(this, currentPermission) == PackageManager.PERMISSION_GRANTED) {
                // Jika izin sudah diberikan, lanjutkan ke izin berikutnya
                permissionIndex++;
                requestNextPermission();
            } else {
                // Jika izin belum diberikan, minta izin
                requestSinglePermissionLauncher.launch(currentPermission);
            }
        } else {
            // Semua izin sudah diberikan
//            Toast.makeText(this, "Semua izin telah diberikan", Toast.LENGTH_SHORT).show();
        }
    }

}