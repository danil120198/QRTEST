package com.laundry.qrtest;

import static androidx.core.app.ServiceCompat.stopForeground;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Shader;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderListener;
import com.laundry.qrtest.Petugas.DataRecyclerView;
import com.laundry.qrtest.Petugas.HomePetugas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class HomeUser extends AppCompatActivity implements OnMapReadyCallback, HBRecorderListener {
    MaterialButton btnEmergencyCall;
    FloatingActionButton btnAdd,btnEnd;
    ImageView iconlocation;
    TextView tvlokasiinfo1, tvlokasiinfo2;
    private GoogleMap mMap;
    String user_id = "";
    String id_petugas = "";
    String lat,lng;
    ImageView imgProfile,imgProfile2;
    int statustext1 = 0;
    String placename="";
    ImageView img_peta;
    TextView mencari_lokasi;
    private FusedLocationProviderClient fusedLocationClient;
    String title="";
    View rootLayout;

    private HBRecorder hbRecorder;
    private static final int REQUEST_PERMISSIONS = 101;
    private static final int CAMERA_PERMISSION_CODE = 1001;
//    private PreviewView previewView;
//    private ExecutorService cameraExecutor;

    private MediaPlayer mediaPlayer;
    FrameLayout container;
    FrameLayout container_remote;
    String token;
    private RtcEngine mRtcEngine;

    String id_call= "";

    private int uid = 0;

    private static final String APP_ID = "4c96eef3216940af9a7bdf6baa675fe1";
    private static final String CHANNEL_NAME = "";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private ActivityResultLauncher<String> notificationPermissionLauncher;
    Bundle bundle;
    SharedPrefManager sharedPrefManager;

    AlertDialog.Builder alert;

    ImageButton btnChat;
    FrameLayout local_video_view_container,remote_video_view_container;

    ImageView btnFlash, btnSwitch;
    TextView btnRecord;

    private Dialog customDialog;
    private Handler handler = new Handler();
    private Runnable closeDialogRunnable;

    private boolean isFlashOn = false;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String videoPath;
    private boolean isFullScreen = false;


    private static final int SCREEN_RECORD_REQUEST_CODE = 1000;
    private static final int REQUEST_MEDIA_PROJECTION_PERMISSION = 1001;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private Uri videoUri;

    private int screenWidth, screenHeight, screenDensity;


    String Incident;
    private Spinner spinnerIncident;
    private ArrayList<String> incidentList = new ArrayList<>();
    private ArrayList<String> incidentDetails = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String API_URL = Data.SERVER+"qr_test/get_incident.php"; // Ganti IP sesuai jaringan lokal


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bundle = getIntent().getExtras();
        sharedPrefManager = new SharedPrefManager(this);
        FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefManager.getUsername())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {

                    }
                });

        TextView logout = findViewById(R.id.logout);
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
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
                finish();
            }
        });

        if (bundle!=null){
            if (sharedPrefManager.getStatus().equals("user")){
                user_id = sharedPrefManager.getUsername();
            }else {
                user_id = bundle.getString("user_id");
            }
        }else {
            user_id = sharedPrefManager.getUsername();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  // Android 11 ke atas
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 200);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            }
        }



        alert = new AlertDialog.Builder(HomeUser.this);



        btnChat = findViewById(R.id.btnChat);
        local_video_view_container = findViewById(R.id.local_video_view_container);
        remote_video_view_container = findViewById(R.id.remote_video_view_container);

//        local_video_view_container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                showFullScreenDialog(true, 0);
//            }
//        });
//
//        // Set klik listener untuk remote video
//        remote_video_view_container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFullScreenDialog(false, uid);
//            }
//        });




        rootLayout = findViewById(R.id.root_layout);
        btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
        btnAdd = findViewById(R.id.btnAdd);
        btnEnd = findViewById(R.id.btnend);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile2 = findViewById(R.id.imgProfile2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.call);

        mediaPlayer.setLooping(false);
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
                    requestLocationPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                } else {
                    // Android 9 ke bawah
                    requestLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION);
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




        hbRecorder = new HBRecorder(this, this);





        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.equals("")){
                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
                        builder.setTitle("Oppss...");
                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
                        builder.show();
                    } else {
                        Dialog dialog = new Dialog(HomeUser.this);
                        dialog.setContentView(R.layout.layout_tambahan);
                        EditText etnama = dialog.findViewById(R.id.etnama);
                        MaterialButton btnLogin = dialog.findViewById(R.id.btnLogin);

                        btnLogin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                title = etnama.getText().toString();
                                tvlokasiinfo1.setText(etnama.getText().toString());
                                tvlokasiinfo2.setText(placename);
                                imgProfile.setVisibility(View.GONE);
//                                previewView = findViewById(R.id.previewView);

//                                proses();
//                                token = TokenGenerator.generateToken(title);
                                if (token != null) {
                                    Log.d("AgoraToken", "Generated Token: " + token);
                                } else {
                                    Log.e("AgoraToken", "Failed to generate token.");
                                }

                                btnAdd.setImageResource((R.drawable.chat));
                                alert.setTitle("Menunggu Petugas");
                                alert.setMessage("Sebentar lagi petugas akan masuk ke panggilan mu, silakan tunggu sebentar yaa");
                                alert.setPositiveButton("oke", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    }
                                });
                                alert.show();


                                Bundle bundle = getIntent().getExtras();
                                SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                                if (bundle!=null){
                                    if (sharedPrefManager.getStatus().equals("user")){
                                        user_id = sharedPrefManager.getUsername();
                                    }else {
                                        user_id = bundle.getString("user_id");
                                    }
                                 }else {
                                user_id = sharedPrefManager.getUsername();
                            }
//                                String finalUser_id = user_id;
                                btnAdd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                                .putExtra("token",token)
                                                .putExtra("user_id", user_id)
                                                .putExtra("channel",sharedPrefManager.getUsername()));
                                    }
                                });

                                btnEmergencyCall.setVisibility(View.GONE);
                                btnEnd.setVisibility(View.VISIBLE);



                                // Memeriksa izin kamera
                                if (ContextCompat.checkSelfPermission(HomeUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(HomeUser.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                                } else {
//                                    startCamera(title);
                                    Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_LONG)
                                            .setAction("Batal", view -> {
                                                // Aksi ketika tombol "OK" diklik
//                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                                if (cameraExecutor != null) {
//                                                    cameraExecutor.shutdown();
                                                imgProfile.setVisibility(View.VISIBLE);
                                                btnEmergencyCall.setVisibility(View.VISIBLE);
                                                btnEnd.setVisibility(View.INVISIBLE);
                                                title = "";
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop(); // Menghentikan audio
                                                    mediaPlayer = MediaPlayer.create(HomeUser.this, R.raw.call); // Reset MediaPlayer
                                                    mediaPlayer.setLooping(false);
                                                }
//                                                }
                                            })
                                            .show();
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.start(); // Memulai audio
                                    }


                                }

//                                cameraExecutor = Executors.newSingleThreadExecutor();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }else {
                    imgProfile.setVisibility(View.GONE);
//                    previewView = findViewById(R.id.previewView);

                    // Memeriksa izin kamera
                    if (ContextCompat.checkSelfPermission(HomeUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeUser.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    } else {
//                        startCamera(title);
//                        proses();
//                        getAgoraToken(title);
                        btnAdd.setImageResource((R.drawable.chat));
                        alert.setTitle("Menunggu Petugas");
                        alert.setMessage("Sebentar lagi petugas akan masuk ke panggilan mu, silakan tunggu sebentar yaa");
                        alert.setPositiveButton("oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        Bundle bundle = getIntent().getExtras();
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                        if (bundle!=null){
                            if (sharedPrefManager.getStatus().equals("user")){
                                user_id = sharedPrefManager.getUsername();
                            }else {
                                user_id = bundle.getString("user_id");
                            }
                        }
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                        .putExtra("token",token)
                                        .putExtra("user_id", user_id)
                                        .putExtra("channel",sharedPrefManager.getUsername()));
                            }
                        });

                        btnEmergencyCall.setVisibility(View.GONE);
                        btnEnd.setVisibility(View.VISIBLE);
                        Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_LONG)
                                .setAction("Batal", view -> {
                                    // Aksi ketika tombol "OK" diklik
//                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                    if (cameraExecutor != null) {
//                                        cameraExecutor.shutdown();
                                    imgProfile.setVisibility(View.VISIBLE);
                                    btnEmergencyCall.setVisibility(View.VISIBLE);
                                    btnEnd.setVisibility(View.INVISIBLE);
                                    title = "";
                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.stop(); // Menghentikan audio
                                        mediaPlayer = MediaPlayer.create(HomeUser.this, R.raw.call); // Reset MediaPlayer
                                        mediaPlayer.setLooping(false);
                                    }
//                                    }
                                })
                                .show();
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start(); // Memulai audio
                        }
                    }

//                    cameraExecutor = Executors.newSingleThreadExecutor();
                }


            }
        });

        btnEmergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.equals("")){
                    if (tvlokasiinfo1.getText().toString().equals("KAMI TIDAK MENEMUKAN ANDA DIPETA")|| placename.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
                        builder.setTitle("Oppss...");
                        builder.setMessage("Lokasi anda belum di aktifkan, silakan aktifkan gps dan ulangi lagi");
                        builder.show();
                    } else {
                        Dialog dialog = new Dialog(HomeUser.this);
                        dialog.setContentView(R.layout.layout_tambahan);
                        EditText etnama = dialog.findViewById(R.id.etnama);
                        MaterialButton btnLogin = dialog.findViewById(R.id.btnLogin);



                        spinnerIncident = dialog.findViewById(R.id.spinnerIncident);

                        // Setup adapter untuk spinner
                        adapter = new ArrayAdapter<>(HomeUser.this, android.R.layout.simple_spinner_item, incidentList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerIncident.setAdapter(adapter);

                        // Load data ke Spinner
                        getDataIncident();

                        // Event ketika dipilih dari spinner
                        spinnerIncident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                if (position > 0) {
//                                    showIncidentDetail(position);
                                    Incident = spinnerIncident.getSelectedItem().toString();
                                    etnama.setText(spinnerIncident.getSelectedItem().toString());

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Tidak melakukan apa-apa jika tidak dipilih
                            }
                        });


                        btnLogin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                title = etnama.getText().toString();
                                tvlokasiinfo1.setText(etnama.getText().toString());
                                tvlokasiinfo2.setText(placename);
                                imgProfile.setVisibility(View.GONE);
//                                previewView = findViewById(R.id.previewView);
//                                token = TokenGenerator.generateToken(title);
//                                getAgoraToken(title);
//                                proses();
                                alert.setTitle("Menunggu Petugas");
                                alert.setMessage("Sebentar lagi petugas akan masuk ke panggilan mu, silakan tunggu sebentar yaa");
                                alert.setPositiveButton("oke", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
//                                alert.show();
                                showCustomDialog();
                                btnAdd.setImageResource((R.drawable.chat));

                                SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                                Bundle bundle = getIntent().getExtras();
                                if (bundle!=null){
                                    if (sharedPrefManager.getStatus().equals("user")){
                                        user_id = sharedPrefManager.getUsername();
                                    }else {
                                        user_id = bundle.getString("user_id");
                                    }
                                }
                                btnAdd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                                .putExtra("token",token)
                                                .putExtra("user_id", user_id)
                                                .putExtra("channel",sharedPrefManager.getUsername()));
                                    }
                                });

                                btnEmergencyCall.setVisibility(View.GONE);
                                btnEnd.setVisibility(View.VISIBLE);
                                if (token != null) {
                                    Log.d("AgoraToken", "Generated Token: " + token);
                                } else {
                                    Log.e("AgoraToken", "Failed to generate token.");
                                }
                                // Memeriksa izin kamera
                                if (ContextCompat.checkSelfPermission(HomeUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(HomeUser.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                                } else {
//                                    startCamera(title);
//                                    proses();

//                                    getAgoraToken(title);
                                    Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_LONG)
                                            .setAction("Batal", view -> {
                                                // Aksi ketika tombol "OK" diklik
//                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                                if (cameraExecutor != null) {
//                                                    cameraExecutor.shutdown();
                                                imgProfile.setVisibility(View.VISIBLE);
                                                btnEmergencyCall.setVisibility(View.VISIBLE);
                                                btnEnd.setVisibility(View.INVISIBLE);
                                                title = "";
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop(); // Menghentikan audio
                                                    mediaPlayer = MediaPlayer.create(HomeUser.this, R.raw.call); // Reset MediaPlayer
                                                    mediaPlayer.setLooping(false);
                                                }
//                                                }
                                            })
                                            .show();
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.start(); // Memulai audio
                                    }
                                }

//                                cameraExecutor = Executors.newSingleThreadExecutor();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }else {
                    imgProfile.setVisibility(View.GONE);
//                    previewView = findViewById(R.id.previewView);

                    // Memeriksa izin kamera
                    if (ContextCompat.checkSelfPermission(HomeUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeUser.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    } else {
//                        startCamera(title);
//                       getAgoraToken(title);
//                        proses();
                        btnAdd.setImageResource((R.drawable.chat));
                        alert.setTitle("Menunggu Petugas");
                        alert.setMessage("Sebentar lagi petugas akan masuk ke panggilan mu, silakan tunggu sebentar yaa");  alert.setPositiveButton("oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setPositiveButton("oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
//                        alert.show();
                        showCustomDialog();
                        Bundle bundle = getIntent().getExtras();
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                        if (bundle!=null){
                            if (sharedPrefManager.getStatus().equals("user")){
                                user_id = sharedPrefManager.getUsername();
                            }else {
                                user_id = bundle.getString("user_id");
                            }
                        }
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                        .putExtra("token",token)
                                        .putExtra("user_id", user_id)
                                        .putExtra("channel",sharedPrefManager.getUsername()));
                            }
                        });

                        btnEmergencyCall.setVisibility(View.GONE);
                        btnEnd.setVisibility(View.VISIBLE);
                        Snackbar.make(rootLayout, "Memanggil Petugas...", Snackbar.LENGTH_LONG)
                                .setAction("Batal", view -> {
                                    // Aksi ketika tombol "OK" diklik
//                                                Snackbar.make(rootLayout, "Tombol OK diklik!", Snackbar.LENGTH_SHORT).show();
//                                    if (cameraExecutor != null) {
//                                        cameraExecutor.shutdown();
                                    imgProfile.setVisibility(View.VISIBLE);
                                    btnEmergencyCall.setVisibility(View.VISIBLE);
                                    btnEnd.setVisibility(View.INVISIBLE);
                                    title = "";
                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.stop(); // Menghentikan audio
                                        mediaPlayer = MediaPlayer.create(HomeUser.this, R.raw.call); // Reset MediaPlayer

                                        mediaPlayer.setLooping(false);
                                    }
//                                    }
                                })
                                .show();
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start(); // Memulai audio
                        }
                    }

//                    cameraExecutor = Executors.newSingleThreadExecutor();
                }



            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (API 29+): Memeriksa ACCESS_BACKGROUND_LOCATION
            requestLocationPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else {
            // Android 9 ke bawah
            requestLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Tampilkan notifikasi
//            showNotification("Judul Notifikasi", "Pesan Notifikasi");
            FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefManager.getUsername())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    });
        } else {
            // Izin belum diberikan
        }




        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (cameraExecutor != null) {
//                    cameraExecutor.shutdown();

              AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
              builder.setTitle("Akhiri Panggilan ?");
              builder.setMessage("Anda yakin mengakhiri panggilan ini ? anda dapat kembali membuat panggilan setelah di akhiri.");
              builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      Bundle bundle = getIntent().getExtras();
                      imgProfile.setVisibility(View.VISIBLE);
                      btnEmergencyCall.setVisibility(View.VISIBLE);
                      btnEnd.setVisibility(View.INVISIBLE);
                      title = "";
//                }
                      if (mRtcEngine != null) {
//                    RtcEngine.destroy();
                          leaveChannel();

                      }
                      if (sharedPrefManager.getUsername().equals("petugas")){
                          prosesEnd(bundle.getString("call_id"));
                      }else {

                          prosesEnd(id_call);
                      }
                  }
              });
              builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {

                  }
              });
              builder.show();
//                restartAppAndClearCache();



            }
        });


        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);

        getAgoraToken(sharedPrefManager.getUsername());

        if (checkSelfPermission()) {
            initializeAgoraEngine();
            setupLocalVideo();

//            joinChannel();
//            imgProfile.setVisibility(View.GONE);
//            imgProfile2.setVisibility(View.GONE);


            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                joinChannel();
            }
//            joinChannel();
        } else {
            requestPermissions();
        }

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefManager.getStatus().equals("user")){
                    if (id_petugas.equals("")){
                        startActivity(new Intent(getApplicationContext(),ListChat.class)
                                .putExtra("user_id",sharedPrefManager.getUsername())
                                .putExtra("channel",title)
                                .putExtra("status","user")
                        );
                    }else {
                        startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                .putExtra("petugas_id",id_petugas)
                                .putExtra("sender_id",sharedPrefManager.getUsername())
                                .putExtra("status","petugas")
                        );
                    }

                }else {
                    startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                            .putExtra("petugas_id",sharedPrefManager.getUsername())
                            .putExtra("channel",bundle.getString("channel"))
                            .putExtra("token",bundle.getString("token"))
                            .putExtra("user_id",bundle.getString("user_id"))
                            .putExtra("call_id",bundle.getString("call_id"))
                            .putExtra("sender_id",bundle.getString("user_id"))
                            .putExtra("status","petugas")
                    );
                }

            }
        });

        btnAdd.setVisibility(View.GONE);


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgProfile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


//        initializeRtcEngine();


        initPermissionLauncher();

        // Periksa dan minta izin
        requestNotificationPermission();

        getEmergencyData();

        btnFlash = findViewById(R.id.flash);
        btnSwitch = findViewById(R.id.camera);
        btnRecord = findViewById(R.id.rec);

        btnFlash.setOnClickListener(v -> toggleFlash());
        btnSwitch.setOnClickListener(v -> switchCamera());
//        btnRecord.setOnClickListener(v -> {
//            if (isRecording) {
//
//                AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                alert.setTitle("Stop merekam Panggilan ?");
//                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        stopRecording();
//                        btnRecord.setText("Rec");
//                    }
//                });
//                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//
//                alert.show();
////                btnRecord.setText("Record");
//            } else {
//                AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                alert.setTitle("Mulai merekam Panggilan ?");
//                alert.setMessage("Fitur ini akan merekam panggilan dan akan menyimpan di penyimpanan Telepon anda");
//                alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        startRecording();
//                        btnRecord.setText("Stop");
//                    }
//                });
//                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//
//                alert.show();
////                btnRecord.setText("Stop");
//            }
//        });

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                new AlertDialog.Builder(this)
                        .setTitle("Mulai merekam Panggilan?")
                        .setMessage("Fitur ini akan merekam panggilan dan menyimpan di penyimpanan telepon Anda.")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            requestPermissionsForAndroid14();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Stop merekam Panggilan?")
                        .setPositiveButton("Ya", (dialog, which) -> stopRecording())
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });

//        requestPermissions23();



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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeUser.this);
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



    private void showFullScreenDialog(boolean isLocal, int uid) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Buat SurfaceView baru untuk dialog
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);

        if (isLocal) {
            // Jika video lokal, setup local video
            mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        } else {
            // Jika video remote, setup remote video
            mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }

        FrameLayout fullScreenContainer = new FrameLayout(this);
        fullScreenContainer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        fullScreenContainer.addView(surfaceView);

        dialog.setContentView(fullScreenContainer);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Tutup dialog ketika video diklik
        fullScreenContainer.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }





    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            lat = location.getLatitude()+"";
                            lng = location.getLongitude()+"";

                            getPlaceName(userLocation);
                        }
                    }
                });


    }


    private void getAgoraToken(String channelName) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL_TOKEN = Data.SERVER+"qr_test/generate_token.php?channel="+channelName;  // Ganti dengan URL server Anda

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parsing token dari respon teks biasa
                         token = response.replace("Token: ", "").trim();

                         joinChannel2();
//                        Toast.makeText(HomeUser.this, "Token: " + token, Toast.LENGTH_SHORT).show();

                        // Join Agora Channel
//                        joinAgoraChannel(token);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Error: " + error.toString());
                        Toast.makeText(HomeUser.this, "Gagal mengambil token", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                                    getPlaceName(userLocation);
                                }
                            }
                        });
            } else {
//                Toast.makeText(this, "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera(title);
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk menjalankan aplikasi.", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == PERMISSION_REQ_ID) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }
            if (permissionsGranted) {
                initializeAgoraEngine();
                setupLocalVideo();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null){
                    joinChannel();
                }

            } else {
//                finish(); // Tutup aplikasi jika izin tidak diberikan
            }
        }
    }

//    private void startCamera(String channelName) {
//        btnEmergencyCall.setVisibility(View.INVISIBLE);
//        btnEnd.setVisibility(View.VISIBLE);
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(HomeUser.this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                // Membuat Preview
//                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                // Pilih kamera depan
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                // Bind lifecycle kamera ke aktivitas
//                cameraProvider.unbindAll();
//                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
//
//                startVideoCall(channelName);
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
//        if (cameraExecutor != null) {
//            cameraExecutor.shutdown();
//        }
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Melepaskan resource MediaPlayer
            mediaPlayer = null;
        }
        if (mRtcEngine != null) {
//            RtcEngine.destroy();
            leaveChannel();
        }

        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }


        handler.removeCallbacks(closeDialogRunnable);
    }


//    private void startVideoCall(String channelName) {
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
//            mRtcEngine.joinChannel("2:4c96eef3216940af9a7bdf6baa675fe1:31c055d1bd330bc34a42f630afe291c2626263d47907cf49f82596e100792ea1:1735707135,0,rtc:Testing,0,1:1735710735;;", "kecelakaan lalu", "Extra", uid);
//            proses();
//
//            imgProfile2.setVisibility(View.GONE);
//
//
//
//        } catch (Exception e) {
//            Log.e("Agora", "Error starting video call: " + e.getMessage());
//        }
//    }






    private void proses(String id_petugas) {
        String HttpURL = Data.SERVER + "qr_test/tambah_emergency.php";
//        final ProgressDialog progressDialog = new ProgressDialog(HomeUser.this);
//        progressDialog.setMessage("Loading... ");
//        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response
                        try {
                            if (response.endsWith("Data Berhasil Disimpan")) {
//                                progressDialog.dismiss();
                                Data.sendNotification(getApplicationContext(),"Emergency Call",title,id_petugas+"");
//                                Toast.makeText(getApplicationContext(), "Data berhasil dikirim", Toast.LENGTH_SHORT).show();
//                                finish();
                            } else {
//                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Gagal melakukan panggilan, silakan coba lagi", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Handle exception
                            Toast.makeText(HomeUser.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("call_id", "");
                params.put("user_id", sharedPrefManager.getUsername());
                params.put("petugas_id", id_petugas);
                params.put("incident_title", title);
                params.put("token", token);
                params.put("status", "");
                params.put("start_time", "");
                params.put("end_time", "");
                params.put("location_lat",lat );
                params.put("location_long", lng);
                params.put("created_at", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }



    private boolean checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : REQUESTED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), APP_ID, new RtcEngineEventHandler());
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x480,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            ));
            mRtcEngine.enableVideo(); // Pastikan video diaktifkan
        } catch (Exception e) {
            throw new RuntimeException("Check Agora APP ID.", e);
        }
    }

    private void setupLocalVideo() {
        container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        mRtcEngine.startPreview();
    }

    private void joinChannel() {
        Bundle bundle = getIntent().getExtras();

                String tokenPetugas = bundle.getString("token");
//                String channel = bundle.getString("channel");
                String channel = bundle.getString("user_id");
                mRtcEngine.joinChannel(tokenPetugas, channel, "Extra Optional Data", 0);
                tvlokasiinfo1.setText(bundle.getString("channel"));
//                imgProfile2.setVisibility(View.GONE);
//                Toast.makeText(this, channel, Toast.LENGTH_SHORT).show();
                imgProfile.setVisibility(View.GONE);
                imgProfile2.setVisibility(View.GONE);
                btnEnd.setVisibility(View.VISIBLE);
                btnEmergencyCall.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setImageResource((R.drawable.chat));

//        Bundle bundle = getIntent().getExtras();
        SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
        if (bundle!=null){
            if (sharedPrefManager.getStatus().equals("user")){
                user_id = sharedPrefManager.getUsername();
            }else {
                user_id = bundle.getString("user_id");
            }
        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                        .putExtra("token",token)
                        .putExtra("user_id", user_id)
                        .putExtra("channel",sharedPrefManager.getUsername()));
            }
        });

                btnEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
                        builder.setTitle("Akhiri Panggilan ?");
                        builder.setMessage("Anda yakin mengakhiri panggilan ini ? anda dapat kembali membuat panggilan setelah di akhiri.");
                        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle = getIntent().getExtras();
                                imgProfile.setVisibility(View.VISIBLE);
                                btnEmergencyCall.setVisibility(View.VISIBLE);
                                btnEnd.setVisibility(View.INVISIBLE);
                                title = "";
//                }
                                if (mRtcEngine != null) {
//                    RtcEngine.destroy();
                                    leaveChannel();

                                }
                                if (bundle!=null){
                                    prosesEnd(bundle.getString("call_id"));
                                }else {

                                    prosesEnd(id_call);
                                }
                            }
                        });
                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();

                    }
                });





    }

    private void joinChannel2() {

        SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
        String tokenPetugas = token;
//                String channel = bundle.getString("channel");
        String channel = sharedPrefManager.getUsername();
        mRtcEngine.joinChannel(token, channel, "Extra Optional Data", 0);





    }

    private void setupRemoteVideo(int uid) {
        container_remote = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container_remote.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
       if (sharedPrefManager.getStatus().equals("user")){
           if (customDialog.isShowing()) {
//                Toast.makeText(HomeUser.this, "Dialog ditutup otomatis!", Toast.LENGTH_SHORT).show();
               customDialog.dismiss();
           }
       }
        getEmergencyData();
    }

    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            System.out.println("Keluar dari channel");
        }
    }

    private boolean checkPermissions() {
        int permissionAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionAudio == PackageManager.PERMISSION_GRANTED &&
                permissionStorage == PackageManager.PERMISSION_GRANTED;
    }





    private void stopRecording() {
        hbRecorder.stopScreenRecording();
        isRecording = false;
        btnRecord.setText("REC");
//        Toast.makeText(this, "Perekaman dihentikan!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HBRecorderOnStart() {
        Toast.makeText(this, "Recording Started!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HBRecorderOnComplete() {
        Toast.makeText(this, "Recording Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {
        Toast.makeText(this, "Recording Error: " + reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HBRecorderOnPause() {

    }

    @Override
    public void HBRecorderOnResume() {

    }


    class RtcEngineEventHandler extends IRtcEngineEventHandler {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
            imgProfile2.setVisibility(View.GONE);
            btnAdd.setImageResource((R.drawable.chat));
            Bundle bundle = getIntent().getExtras();
            SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
            if (bundle!=null){
                if (sharedPrefManager.getStatus().equals("user")){
                    user_id = sharedPrefManager.getUsername();
                }else {
                    user_id = bundle.getString("user_id");
                }
            }else {
                user_id = sharedPrefManager.getUsername();
            }
            btnAdd.setVisibility(View.GONE);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                            .putExtra("token",token)
                            .putExtra("user_id", user_id)
                            .putExtra("channel",sharedPrefManager.getUsername()));
                }
            });
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeUser.this);
                    builder.setTitle("Akhiri Panggilan ?");
                    builder.setMessage("Anda yakin mengakhiri panggilan ini ? anda dapat kembali membuat panggilan setelah di akhiri.");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle bundle = getIntent().getExtras();
                            imgProfile.setVisibility(View.VISIBLE);
                            btnEmergencyCall.setVisibility(View.VISIBLE);
                            btnEnd.setVisibility(View.INVISIBLE);
                            title = "";
//                }
                            if (mRtcEngine != null) {
//                    RtcEngine.destroy();
                                leaveChannel();

                            }
                            if (bundle!=null){
                                prosesEnd(bundle.getString("call_id"));
                            }else {

                                prosesEnd(id_call);
                            }
                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();

                }
            });
//
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> removeRemoteVideo());
            imgProfile2.setVisibility(View.VISIBLE);

        }
    }

    private void removeRemoteVideo() {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
       if (sharedPrefManager.getStatus().equals("user")){
//           restartAppAndClearCache();
//           finish();
//           startActivity(new Intent(getApplicationContext(),HomeUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
           AppUtils.restartApp(HomeUser.this);
       }else {
//           restartAppAndClearCache();

//           finish();
//           startActivity(new Intent(getApplicationContext(), HomePetugas.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

           AppUtils.restartApp(HomeUser.this);
       }
    }


    private void prosesEnd(String call_id) {
        String HttpURL = Data.SERVER + "qr_test/update_call_petugas_end.php";
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
//                                if (sharedPrefManager.getStatus().equals("petugas")){
//                                    finish();
//                                }
                                AppUtils.restartApp(HomeUser.this);
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
                SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                Map<String, String> params = new HashMap<>();
                params.put("call_id", call_id);
                params.put("petugas_id", "");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void prosesEndUser() {
        String HttpURL = Data.SERVER + "qr_test/update_call_user_end.php";
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
//                                if (sharedPrefManager.getStatus().equals("petugas")){
//                                    finish();
//                                }
                                AppUtils.restartApp(HomeUser.this);
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
                SharedPrefManager sharedPrefManager = new SharedPrefManager(HomeUser.this);
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sharedPrefManager.getUsername());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (sharedPrefManager.getStatus().equals("user")){
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Keluar")
                    .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Keluar dari aplikasi

                        super.onBackPressed();
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> {
                        // Tutup dialog
                        dialog.dismiss();
                    })
                    .setCancelable(false) // Cegah dialog ditutup dengan tombol luar
                    .show();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Keluar Panggilan")
                    .setMessage("Apakah Anda yakin ingin keluar dari Panggilan?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Keluar dari aplikasi
                        Bundle bundle = getIntent().getExtras();
                        if (bundle!=null){
                            prosesEnd(bundle.getString("call_id"));
                        }
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> {
                        // Tutup dialog
                        dialog.dismiss();
                    })
                    .setCancelable(false) // Cegah dialog ditutup dengan tombol luar
                    .show();
        }

    }


    private void getEmergencyData() {

        String url = Data.SERVER + "qr_test/get_emergency_user_self.php?id_user="+sharedPrefManager.getUsername();
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.isNull(0)) {
//                        progressDialog.dismiss();
                        id_petugas = "";
                    }

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


                        id_petugas = petugas_id;
                         id_call = call_id;

                        btnChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(),ChatActivity.class)
                                        .putExtra("user_id",sharedPrefManager.getUsername())
                                        .putExtra("sender_id",id_petugas)
                                        .putExtra("status","petugas")
                                        .putExtra("call_id",call_id)
                                );
                            }
                        });

                    }


//                    Toast.makeText(HomeUser.this, id_petugas, Toast.LENGTH_SHORT).show();

//                        progressDialog.dismiss();



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


    private void restartAppAndClearCache() {
        // Hapus cache aplikasi
//        try {
//            Runtime.getRuntime().exec("pm clear " + getPackageName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Restart aplikasi
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // Matikan proses aplikasi agar benar-benar fresh
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            am.killBackgroundProcesses(getPackageName());
        }

        // Tutup aplikasi
        System.exit(0);
    }

    private void toggleFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0]; // Kamera belakang
            if (!isFlashOn) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashOn = true;
            } else {
                cameraManager.setTorchMode(cameraId, false);
                isFlashOn = false;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    private void switchCamera() {
        if (mRtcEngine != null) {
            mRtcEngine.switchCamera();
        }
    }







    private void requestPermissionsForAndroid14() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION
                }, REQUEST_MEDIA_PROJECTION_PERMISSION);
            } else {
                startRecordingScreen(); // Jika sudah diberi izin, langsung mulai recording
            }
        } else {
            startRecordingScreen();
        }
    }

    private void startRecordingScreen() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

//    private void startScreenRecording() {
//        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
//        startActivityForResult(captureIntent, SCREEN_RECORD_REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
//            Intent serviceIntent = new Intent(this, ScreenRecordService.class);
//            serviceIntent.setAction(ScreenRecordService.ACTION_START);
//            serviceIntent.putExtra("RESULT_CODE", resultCode);
//            serviceIntent.putExtra("DATA_INTENT", data);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(serviceIntent);
//            } else {
//                startService(serviceIntent);
//            }


            hbRecorder.startScreenRecording(data, resultCode);

            isRecording = true;
            btnRecord.setText("Stop");
//            Toast.makeText(this, "Perekaman dimulai...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Izin merekam ditolak!", Toast.LENGTH_SHORT).show();
        }
    }

//    private void stopRecording() {
//        Intent stopIntent = new Intent(this, ScreenRecordService.class);
//        stopIntent.setAction(ScreenRecordService.ACTION_STOP);
//        startService(stopIntent);
//
//        isRecording = false;
//        btnRecord.setText("REC");
//        Toast.makeText(this, "Perekaman dihentikan!", Toast.LENGTH_SHORT).show();
//    }


    private void showCustomDialog() {
        // Membuat instance dialog
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.layout_call_nearby);
        customDialog.setCancelable(false); // Tidak bisa ditutup dengan tombol back

        // Mengatur ukuran dialog agar full screen
        if (customDialog.getWindow() != null) {
            customDialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Transparan agar full screen lebih bagus
        }

        // Inisialisasi view di dalam dialog
        TextView nm_petugas = customDialog.findViewById(R.id.nm_petugas);
        ImageView loading = customDialog.findViewById(R.id.loading);

        Glide.with(HomeUser.this).asGif().load(R.drawable.loading).into(loading);

        getEmergencyDataPetugasNearby(nm_petugas,sharedPrefManager.getIdClient());

        // Menangani tombol OK
//        btnOK.setOnClickListener(view -> {
//            String inputText = etInput.getText().toString();
//            Toast.makeText(MainActivity.this, "Input: " + inputText, Toast.LENGTH_SHORT).show();
//            customDialog.dismiss();
//            handler.removeCallbacks(closeDialogRunnable); // Hentikan timer jika ditutup manual
//        });

        // Menjalankan timer untuk menutup dialog setelah 20 detik
        closeDialogRunnable = () -> {
            if (customDialog.isShowing()) {
                Toast.makeText(HomeUser.this, "Panggilan tidak di jawab, silakan coba lagi", Toast.LENGTH_SHORT).show();
                imgProfile.setVisibility(View.VISIBLE);
                int a = Integer.parseInt(sharedPrefManager.getIdClient()) + 1;
                sharedPrefManager.saveSPString(SharedPrefManager.ID_CLIENT,a+"");
                prosesEndUser();
//                customDialog.dismiss();

            }
        };
        handler.postDelayed(closeDialogRunnable, 30000); // 30 detik

        // Tampilkan dialog
        customDialog.show();
    }

    private void getEmergencyDataPetugasNearby(TextView nama,String limit) {

        String url = Data.SERVER + "qr_test/coba_terdekat.php?curlat="+lat+"&curlong="+lng+"&limit="+limit;
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if (jsonArray.isNull(0)) {
//                        progressDialog.dismiss();
                        id_petugas = "";
                        nama.setText("Petugas Tidak di temukan, silakan tutup dan coba lagi");
                    }

                    String user = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        // Simpan pada variabel
                        String nama_lengkap = c.getString("nama_lengkap");
                        user = c.getString("username");
                        String id_petugas = c.getString("id_petugas");
                        String latlng = c.getString("latlng");

//                        nama.setText(username);
                        nama.setText("Berdering");




//                        id_petugas = petugas_id;
//                        id_call = call_id;



                    }

                    Data.sendNotification(getApplicationContext(),"Emergency Call",title,user+"");
                    proses(user);


//                    Toast.makeText(HomeUser.this, id_petugas, Toast.LENGTH_SHORT).show();

//                        progressDialog.dismiss();



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






    private void getDataIncident() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Tampilkan response JSON di Logcat (untuk debugging)
                            System.out.println("Response: " + response.toString());

                            if (response.getBoolean("status")) {
                                JSONArray data = response.getJSONArray("data");
                                incidentList.clear(); // Bersihkan data sebelumnya
                                incidentDetails.clear();

                                // Tambahkan placeholder untuk pilihan pertama
                                incidentList.add("Pilih Incident");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject incident = data.getJSONObject(i);
                                    String incidentName = incident.getString("incident_name");
                                    String reportedDate = incident.getString("reported_date");

                                    // Tambahkan ke arrayList
                                    incidentList.add(incidentName);
                                    incidentDetails.add("Incident: " + incidentName + "\nTanggal Lapor: " + reportedDate);
                                }

                                // Update adapter untuk Spinner
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HomeUser.this, "Tidak ada data incident", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeUser.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeUser.this, "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    // Tampilkan Data dalam bentuk Dialog
    private void showIncidentDetail(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Incident");
        builder.setMessage(incidentDetails.get(position - 1)); // Posisi dikurangi 1 karena ada placeholder di awal
        builder.setPositiveButton("OK", null);
        builder.show();
    }




}