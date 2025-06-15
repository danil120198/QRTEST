package com.laundry.qrtest.Petugas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.laundry.qrtest.Adapter;
import com.laundry.qrtest.Dashboard;
import com.laundry.qrtest.Data;
import com.laundry.qrtest.HomeUser;
import com.laundry.qrtest.R;
import com.laundry.qrtest.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HomePetugas extends AppCompatActivity implements OnMapReadyCallback {
    MaterialButton btnEmergencyCall;
    FloatingActionButton btnAdd,btnEnd;
    ImageView iconlocation;
    TextView tvlokasiinfo1, tvlokasiinfo2;
    private GoogleMap mMap;

//    ImageView imgProfile,imgProfile2;
    int statustext1 = 0;
    String placename="";
    ImageView img_peta;
    TextView mencari_lokasi;
    private FusedLocationProviderClient fusedLocationClient;
    String title="";
    View rootLayout;
    private static final int CAMERA_PERMISSION_CODE = 1001;
//    private PreviewView previewView;
    private ExecutorService cameraExecutor;

    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private AdapterPetugas recyclerAdapter;
    private RecyclerView recyclerView;
    private ArrayList<DataRecyclerView> listdata;
    private GridLayoutManager layoutmanager;
    SharedPrefManager sharedPrefManager;

    private Handler handler = new Handler();
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_waiting);

        sharedPrefManager
                 = new SharedPrefManager(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomePetugas.this);

        FirebaseMessaging.getInstance().subscribeToTopic("petugas")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {

                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefManager.getUsername())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {

                    }
                });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutmanager=new GridLayoutManager(getApplicationContext(),1);
        layoutmanager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);
        listdata = new ArrayList<DataRecyclerView>();
        recyclerAdapter = new AdapterPetugas(this,listdata);
        recyclerView.setAdapter(recyclerAdapter);

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN,false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPrefManager.getUsername())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                            } else {

                            }
                        });
                FirebaseMessaging.getInstance().unsubscribeFromTopic("petugas")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                            } else {

                            }
                        });
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                finish();
            }
        });

//        // Data statis
//        List<String> dataList = new ArrayList<>();
//        dataList.add("Kecelakaan Lalu Lintas");

//        // Setup RecyclerView
//        Adapter adapter = new Adapter(HomePetugas.this,dataList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);

        rootLayout = findViewById(R.id.root_layout);
        btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
        btnAdd = findViewById(R.id.btnAdd);
        btnEnd = findViewById(R.id.btnend);

//        imgProfile = findViewById(R.id.imgProfile);
//        imgProfile2 = findViewById(R.id.imgProfile2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        img_peta = findViewById(R.id.img_peta);
        mencari_lokasi = findViewById(R.id.mencari_lokasi);
        iconlocation = findViewById(R.id.iconlokasi);
        tvlokasiinfo1 = findViewById(R.id.tvLocationInfo);
        tvlokasiinfo2 = findViewById(R.id.tvLocationInfo2);

        tvlokasiinfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ (API 29+): Memeriksa ACCESS_BACKGROUND_LOCATION
                    requestLocationPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                } else {
                    // Android 9 ke bawah
                    requestLocationPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        });
        tvlokasiinfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statustext1 == 0){
                    statustext1 = 1;
                    tvlokasiinfo1.setSingleLine(false);
                }else if(statustext1 == 1){
                    statustext1 = 0;
                    tvlokasiinfo1.setSingleLine();
                }
            }
        });

        getData();

        runnable = new Runnable() {
            @Override
            public void run() {
                refresh(); // Method untuk ambil data baru atau refresh UI
                handler.postDelayed(this, 7000); // Jalankan ulang setelah 5 detik
            }
        };

        handler.post(runnable); // Mulai handler


//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (title.equals("")){
//                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
//                        builder.setTitle("Oppss...");
//                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
//                        builder.show();
//                    } else {
//                        Dialog dialog = new Dialog(HomeUser.this);
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
//                                if (ContextCompat.checkSelfPermission(HomeUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(HomeUser.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
//                    if (ContextCompat.checkSelfPermission(HomeUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(HomeUser.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                    } else {
//                        startCamera();
//                    }
//
//                    cameraExecutor = Executors.newSingleThreadExecutor();
//                }
//
//
//            }
//        });
//
//        btnEmergencyCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (title.equals("")){
//                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
//                        builder.setTitle("Oppss...");
//                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
//                        builder.show();
//                    } else {
//                        Dialog dialog = new Dialog(HomeUser.this);
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
//                                if (ContextCompat.checkSelfPermission(HomeUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    ActivityCompat.requestPermissions(HomeUser.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
//                    if (ContextCompat.checkSelfPermission(HomeUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(HomeUser.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
//            requestLocationPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        } else {
//            // Android 9 ke bawah
//            requestLocationPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
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
//                }
//            }
//        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Tampilkan notifikasi
//            showNotification("Judul Notifikasi", "Pesan Notifikasi");
            FirebaseMessaging.getInstance().subscribeToTopic("petugas")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    });
        } else {
            // Izin belum diberikan
        }

        initPermissionLauncher();
        requestNotificationPermission();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            img_peta.setVisibility(View.GONE);
                            mencari_lokasi.setVisibility(View.GONE);

                            updateLokasi(sharedPrefManager.getUsername(),location.getLatitude()+","+location.getLongitude());
                            getPlaceName(userLocation);
                        }
                    }
                });

    }
    private void initPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Izin diberikan
//                        Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show();
                    } else {
                        // Izin ditolak, arahkan ke pengaturan
//                        Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HomePetugas.this);
                        dialog.setTitle("Izinkan Notifikasi");
                        dialog.setMessage("Izinkan notifikasi agar aplikasi berjalan dengan baik");
                        dialog.setPositiveButton("izinkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectToNotificationSettings();
                            }
                        });
                        dialog.show();
//                        redirectToAppSettings();
                    }
                }
        );
    }

    private void requestNotificationPermission() {
        // Periksa versi Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Periksa apakah izin sudah diberikan
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Minta izin
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // Izin sudah diberikan
//                Toast.makeText(this, "Izin notifikasi sudah diberikan", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Tidak diperlukan izin untuk versi di bawah Android 13
//            Toast.makeText(this, "Izin notifikasi tidak diperlukan untuk versi ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToNotificationSettings() {
        Intent intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Untuk Android 8.0 (API Level 26) ke atas
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            // Untuk Android di bawah 8.0, buka pengaturan aplikasi
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
        }

        startActivity(intent);
    }

    private void getPlaceName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String placeName = addresses.get(0).getAddressLine(0);
                // Tampilkan nama lokasi
                System.out.println("" + placeName);
                tvlokasiinfo1.setText(placeName);
                placename = placeName;
                tvlokasiinfo2.setText("Lokasi anda sekarang");
                iconlocation.setImageDrawable(getResources().getDrawable(R.drawable.baseline_location_on_24));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationPermission(String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Izin lokasi sudah diberikan.", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "Aplikasi memerlukan akses lokasi untuk fitur ini.", Toast.LENGTH_SHORT).show();

            }

            // Meminta izin
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin lokasi diterima.", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                                    mMap.setMyLocationEnabled(true);
                                    img_peta.setVisibility(View.GONE);
                                    mencari_lokasi.setVisibility(View.GONE);
                                    updateLokasi(sharedPrefManager.getUsername(),location.getLatitude()+","+location.getLongitude());

                                    getPlaceName(userLocation);
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk menjalankan aplikasi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void startCamera() {
//        btnEmergencyCall.setVisibility(View.INVISIBLE);
//        btnEnd.setVisibility(View.VISIBLE);
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(HomePetugas.this);
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
//
//
//            } catch (Exception e) {
//                Log.e("CameraX", "Use case binding failed", e);
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void getData() {

        String url = Data.SERVER + "qr_test/get_emergency_petugas.php?id_user="+sharedPrefManager.getUsername();
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.isNull(0)) {
//                        progressDialog.dismiss();
                    }

                    ImageView back = findViewById(R.id.back);
                    if (jsonArray.length() == 0) {
                        back.setVisibility(View.VISIBLE);
                    } else {
                        back.setVisibility(View.GONE);
                    }

                    listdata.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        // Simpan pada variabel
                        String call_id = c.getString("call_id");
                        String user_id = c.getString("user_id");
                        String petugas_id = c.getString("petugas_id");
                        String incident_title = c.getString("incident_title");
                        String token = c.getString("token");
                        String status = c.getString("status");
                        String start_time = c.getString("start_time");
                        String end_time = c.getString("end_time");
                        String location_lat = c.getString("location_lat");
                        String location_long = c.getString("location_long");
                        String created_at = c.getString("created_at");

                        if (status.equals("pending")) {

                            DataRecyclerView item = new DataRecyclerView();
                            item.setCallId(call_id);
                            item.setUserId(user_id);
                            item.setPetugasId(petugas_id);
                            item.setIncidentTitle(incident_title);
                            item.setToken(token);
                            item.setStatus(status);
                            item.setStartTime(start_time);
                            item.setEndTime(end_time);
                            item.setLocationLat(location_lat);
                            item.setLocationLong(location_long);
                            item.setCreatedAt(created_at);

                            listdata.add(item);
                            recyclerAdapter.notifyDataSetChanged();
                        }

//                        progressDialog.dismiss();
                    }

                    recyclerAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
//                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                Log.d("masalah : ", error.toString());
            }
        });

        rq.add(jsonObjectRequest);
    }

    private void updateLokasi(String username, String latlng) {
        String HttpURL = Data.SERVER + "qr_test/update_lokasi_petugas.php";
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Loading... ");
//        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response
                        try {
                            if (response.endsWith("Data Berhasil Diupdate")) {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "Data berhasil dikirim", Toast.LENGTH_SHORT).show();
//                                finish();
                            } else {
//                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            // Handle exception
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
//                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("latlng", latlng);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    void refresh(){
        getData();
    }

}