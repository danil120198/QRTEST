package com.laundry.qrtest.Petugas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
//import com.google.common.util.concurrent.ListenableFuture;
import com.laundry.qrtest.ChatActivity;
import com.laundry.qrtest.HomeUser;
import com.laundry.qrtest.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//
//import io.agora.base.internal.SurfaceViewRenderer;
//import io.agora.rtc2.IRtcEngineEventHandler;
//import io.agora.rtc2.RtcEngine;
//import io.agora.rtc2.video.VideoCanvas;
//import io.agora.rtc2.video.VideoEncoderConfiguration;

public class CallActivityPetugas extends AppCompatActivity {
//    MaterialButton btnEmergencyCall;
//    FloatingActionButton btnAdd,btnEnd;
//    ImageView iconlocation;
//    TextView tvlokasiinfo1, tvlokasiinfo2;
//    private GoogleMap mMap;
//
//    ImageView imgProfile,imgProfile2;
//    int statustext1 = 0;
//    String placename="";
//    ImageView img_peta;
//    TextView mencari_lokasi;
//    private FusedLocationProviderClient fusedLocationClient;
//    String title="";
//    View rootLayout;
//    private static final int CAMERA_PERMISSION_CODE = 1001;
//    private PreviewView previewView;
//    private ExecutorService cameraExecutor;
//
//    String token;
//    private RtcEngine mRtcEngine;
//    private String appId = "4c96eef3216940af9a7bdf6baa675fe1"; // Ganti dengan App ID Anda
//    private String channelName = "";
//    private SurfaceViewRenderer localSurfaceView, remoteSurfaceView;
//    Bundle bundle;
//    private int uid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_petugas);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//         bundle = getIntent().getExtras();
//
//
////        localSurfaceView = findViewById(R.id.local_video_view);
//        remoteSurfaceView = findViewById(R.id.remote_video_view);
//
//        rootLayout = findViewById(R.id.root_layout);
//        btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
//        btnAdd = findViewById(R.id.btnAdd);
//        btnEnd = findViewById(R.id.btnend);
//
//        imgProfile = findViewById(R.id.imgProfile);
//        imgProfile2 = findViewById(R.id.imgProfile2);
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//
//        img_peta = findViewById(R.id.img_peta);
//        mencari_lokasi = findViewById(R.id.mencari_lokasi);
//        iconlocation = findViewById(R.id.iconlokasi);
//        tvlokasiinfo1 = findViewById(R.id.tvLocationInfo);
//        tvlokasiinfo2 = findViewById(R.id.tvLocationInfo2);
//
//        tvlokasiinfo2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    // Android 10+ (API 29+): Memeriksa ACCESS_BACKGROUND_LOCATION
//                    requestLocationPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//                } else {
//                    // Android 9 ke bawah
//                    requestLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//                }
//            }
//        });
//        tvlokasiinfo1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (statustext1 == 0){
//                    statustext1 = 1;
//                    tvlokasiinfo1.setSingleLine(false);
//                }else if(statustext1 == 1){
//                    statustext1 = 0;
//                    tvlokasiinfo1.setSingleLine();
//                }
//            }
//        });
//
//
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), ChatActivity.class));



//                if (title.equals("")){
//                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(CallActivityPetugas.this);
//                        builder.setTitle("Oppss...");
//                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
//                        builder.show();
//                    } else {
//                        Dialog dialog = new Dialog(CallActivityPetugas.this);
//                        dialog.setContentView(R.layout.layout_tambahan);
//                        EditText etnama = dialog.findViewById(R.id.etnama);
//                        MaterialButton btnLogin = dialog.findViewById(R.id.btnLogin);
//
//                        btnLogin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                title = etnama.getText().toString();
//                                tvlokasiinfo1.setText(etnama.getText().toString());
//                                tvlokasiinfo2.setText(placename);
//                                imgProfile.setVisibility(View.GONE);
//                                previewView = findViewById(R.id.previewView);
//
//                                // Memeriksa izin kamera
//                                if (ContextCompat.checkSelfPermission(CallActivityPetugas.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(CallActivityPetugas.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                                } else {
//                                    startCamera();
//                                    Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_INDEFINITE)
//                                            .setAction("Batal", view -> {
//                                                // Aksi ketika tombol "OK" diklik
////                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                                if (cameraExecutor != null) {
//                                                    cameraExecutor.shutdown();
//                                                    imgProfile.setVisibility(View.VISIBLE);
//                                                    btnEmergencyCall.setVisibility(View.VISIBLE);
//                                                    btnEnd.setVisibility(View.INVISIBLE);
//                                                    title = "";
//                                                }
//                                            })
//                                            .show();
//                                }
//
//                                cameraExecutor = Executors.newSingleThreadExecutor();
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                }else {
//                    imgProfile.setVisibility(View.GONE);
//                    previewView = findViewById(R.id.previewView);
//
//                    // Memeriksa izin kamera
//                    if (ContextCompat.checkSelfPermission(CallActivityPetugas.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(CallActivityPetugas.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                    } else {
//                        startCamera();
//                    }
//
//                    cameraExecutor = Executors.newSingleThreadExecutor();
//                }


            }
//        });
//
//                                imgProfile2.setVisibility(View.GONE);
//                                previewView = findViewById(R.id.previewView2);
//
//                                // Memeriksa izin kamera
//                                if (ContextCompat.checkSelfPermission(CallActivityPetugas.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(CallActivityPetugas.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                                } else {
//                                    startCamera();
//
//                                }
//
//                                cameraExecutor = Executors.newSingleThreadExecutor();




//        btnEmergencyCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (title.equals("")){
//                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(CallActivityPetugas.this);
//                        builder.setTitle("Oppss...");
//                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
//                        builder.show();
//                    } else {
//                        Dialog dialog = new Dialog(CallActivityPetugas.this);
//                        dialog.setContentView(R.layout.layout_tambahan);
//                        EditText etnama = dialog.findViewById(R.id.etnama);
//                        MaterialButton btnLogin = dialog.findViewById(R.id.btnLogin);
//
//                        btnLogin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                title = etnama.getText().toString();
//                                tvlokasiinfo1.setText(etnama.getText().toString());
//                                tvlokasiinfo2.setText(placename);
//                                imgProfile.setVisibility(View.GONE);
//                                previewView = findViewById(R.id.previewView);
//
//                                // Memeriksa izin kamera
//                                if (ContextCompat.checkSelfPermission(CallActivityPetugas.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(CallActivityPetugas.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                                } else {
//                                    startCamera();
//                                    Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_INDEFINITE)
//                                            .setAction("Batal", view -> {
//                                                // Aksi ketika tombol "OK" diklik
////                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                                if (cameraExecutor != null) {
//                                                    cameraExecutor.shutdown();
//                                                    imgProfile.setVisibility(View.VISIBLE);
//                                                    btnEmergencyCall.setVisibility(View.VISIBLE);
//                                                    btnEnd.setVisibility(View.INVISIBLE);
//                                                    title = "";
//                                                }
//                                            })
//                                            .show();
//                                }
//
//                                cameraExecutor = Executors.newSingleThreadExecutor();
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                }else {
//                    imgProfile.setVisibility(View.GONE);
//                    previewView = findViewById(R.id.previewView);
//
//                    // Memeriksa izin kamera
//                    if (ContextCompat.checkSelfPermission(CallActivityPetugas.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(CallActivityPetugas.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                    } else {
//                        startCamera();
//
//                    }
//
//                    cameraExecutor = Executors.newSingleThreadExecutor();
//                }
//
//
//
//            }
//        });
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            // Android 10+ (API 29+): Memeriksa ACCESS_BACKGROUND_LOCATION
//            requestLocationPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        } else {
//            // Android 9 ke bawah
//            requestLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//
//        btnEnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (cameraExecutor != null) {
//                    cameraExecutor.shutdown();
//                    imgProfile.setVisibility(View.VISIBLE);
//                    btnEmergencyCall.setVisibility(View.VISIBLE);
//                    btnEnd.setVisibility(View.INVISIBLE);
//                    title = "";
//                    finish();
//                }
//            }
//        });
//
//
//
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), appId, new IRtcEngineEventHandler() {
//                @Override
//                public void onUserJoined(int uid, int elapsed) {
//                    super.onUserJoined(uid, elapsed);
//                    runOnUiThread(() -> {
//                        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//                        imgProfile.setVisibility(View.GONE);
//                    });
//                }
//
//                @Override
//                public void onUserOffline(int uid, int reason) {
//                    super.onUserOffline(uid, reason);
//                    runOnUiThread(() -> remoteSurfaceView.setVisibility(View.INVISIBLE));
//                    Snackbar.make(rootLayout, "Menghubungkan...", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("Batal", view -> {
//                                // Aksi ketika tombol "OK" diklik
////                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                if (cameraExecutor != null) {
//                                    cameraExecutor.shutdown();
//                                    imgProfile.setVisibility(View.VISIBLE);
////                                                    btnEmergencyCall.setVisibility(View.VISIBLE);
//                                    btnEnd.setVisibility(View.VISIBLE);
//                                    title = "";
//                                }
//                            })
//                            .show();
//                    imgProfile.setVisibility(View.VISIBLE);
//                }
//            });
//        } catch (Exception e) {
//            Log.e("Agora", "Error initializing Agora SDK: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
//                            img_peta.setVisibility(View.GONE);
//                            mencari_lokasi.setVisibility(View.GONE);
//
//                            getPlaceName(userLocation);
//                        }
//                    }
//                });
//
//    }
//
//    private void getPlaceName(LatLng latLng) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                String placeName = addresses.get(0).getAddressLine(0);
//                // Tampilkan nama lokasi
//                System.out.println("" + placeName);
//                tvlokasiinfo1.setText(placeName);
//                placename = placeName;
//                tvlokasiinfo2.setText("Lokasi anda sekarang");
//                iconlocation.setImageDrawable(getResources().getDrawable(R.drawable.baseline_location_on_24));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void requestLocationPermission(String permission) {
//        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
////            Toast.makeText(this, "Izin lokasi sudah diberikan.", Toast.LENGTH_SHORT).show();
//
//        } else {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                Toast.makeText(this, "Aplikasi memerlukan akses lokasi untuk fitur ini.", Toast.LENGTH_SHORT).show();
//
//            }
//
//            // Meminta izin
//            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Izin lokasi diterima.", Toast.LENGTH_SHORT).show();
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                fusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                            @SuppressLint("MissingPermission")
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
//                                    mMap.setMyLocationEnabled(true);
//                                    img_peta.setVisibility(View.GONE);
//                                    mencari_lokasi.setVisibility(View.GONE);
//
//                                    getPlaceName(userLocation);
//                                }
//                            }
//                        });
//            } else {
//                Toast.makeText(this, "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        else if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
//            } else {
//                Toast.makeText(this, "Izin kamera diperlukan untuk menjalankan aplikasi.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void startCamera() {
//        btnEmergencyCall.setVisibility(View.INVISIBLE);
//        btnEnd.setVisibility(View.VISIBLE);
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CallActivityPetugas.this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                // Membuat Preview
//                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                // Pilih kamera belakang
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                // Bind lifecycle kamera ke aktivitas
//                cameraProvider.unbindAll();
//                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
//
//                startVideoCall();
//
//
//            } catch (Exception e) {
//                Log.e("CameraX", "Use case binding failed", e);
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (cameraExecutor != null) {
//            cameraExecutor.shutdown();
//        }
//    }
//
//
//    private void startVideoCall() {
//        try {
//
//            mRtcEngine.enableVideo();
//            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
//                    new VideoEncoderConfiguration.VideoDimensions(640, 360),
//                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
//                    VideoEncoderConfiguration.STANDARD_BITRATE,
//                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));
//
//            mRtcEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//            mRtcEngine.joinChannel("2:4c96eef3216940af9a7bdf6baa675fe1:31c055d1bd330bc34a42f630afe291c2626263d47907cf49f82596e100792ea1:1735707135,0,rtc:Testing,0,1:1735710735;;", bundle.getString("kecelakaan lalu"), "Extra", uid);
//
//            imgProfile.setVisibility(View.GONE);
//
//
//
//        } catch (Exception e) {
//            Log.e("Agora", "Error starting video call: " + e.getMessage());
//        }
//    }


}