package com.laundry.qrtest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterPetugas extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_WAJAH = 1;
    private static final int PICK_IMAGE_REQUEST_KTP = 2;
    private static final int PICK_DOC_SURAT_IZIN = 3;
    private static final int PICK_DOC_TANDA_REGISTRASI = 4;

    private TextInputEditText etNamaLengkap, etUsername, etNoHp, etTanggalLahir, etAlamat, etNIK, etIzinPraktek, etTandaRegistrasi, etPasswordLogin;
    private ImageView imgFotoWajah, imgFotoKTP;
    private TextView tvFileSuratIzinPraktek, tvFileTandaRegistrasi;
    private Bitmap bitmapFotoWajah, bitmapFotoKTP;
    private String suratIzinBase64, tandaRegistrasiBase64;

    private final String URL_API = Data.SERVER+"qr_test/api_register_petugas.php"; // Ganti dengan URL API Anda

    String Incident;
    private Spinner spinnerIncident;
    private ArrayList<String> incidentList = new ArrayList<>();
    private ArrayList<String> incidentDetails = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String API_URL = Data.SERVER+"qr_test/get_incident.php"; // Ganti IP sesuai jaringan lokal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_petugas);

        // Inisialisasi View
        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etUsername = findViewById(R.id.etUsername);
        etNoHp = findViewById(R.id.etNoHp);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        etAlamat = findViewById(R.id.etAlamat);
        etNIK = findViewById(R.id.etNIK);
        etIzinPraktek = findViewById(R.id.etIzinPraktek);
        etTandaRegistrasi = findViewById(R.id.etTandaRegistrasi);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        imgFotoWajah = findViewById(R.id.imgFotoWajah);
        imgFotoKTP = findViewById(R.id.imgFotoKTP);
        tvFileSuratIzinPraktek = findViewById(R.id.tvFileSuratIzinPraktek);
        tvFileTandaRegistrasi = findViewById(R.id.tvFileTandaRegistrasi);
        Button btnUploadFotoWajah = findViewById(R.id.btnUploadFotoWajah);
        Button btnUploadFotoKTP = findViewById(R.id.btnUploadFotoKTP);
        Button btnUploadSuratIzinPraktek = findViewById(R.id.btnUploadSuratIzinPraktek);
        Button btnUploadTandaRegistrasi = findViewById(R.id.btnUploadTandaRegistrasi);
        Button btnRegisterPetugas = findViewById(R.id.btnRegisterPetugas);

        // Upload Foto Wajah
        btnUploadFotoWajah.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST_WAJAH));

        // Upload Foto KTP
        btnUploadFotoKTP.setOnClickListener(v -> pickImage(PICK_IMAGE_REQUEST_KTP));

        // Upload Surat Izin Praktek
        btnUploadSuratIzinPraktek.setOnClickListener(v -> pickDocument(PICK_DOC_SURAT_IZIN));

        // Upload Tanda Registrasi
        btnUploadTandaRegistrasi.setOnClickListener(v -> pickDocument(PICK_DOC_TANDA_REGISTRASI));

        // Register Petugas
        btnRegisterPetugas.setOnClickListener(v -> uploadData());




        spinnerIncident = findViewById(R.id.spinnerIncident);

        // Setup adapter untuk spinner
        adapter = new ArrayAdapter<>(RegisterPetugas.this, android.R.layout.simple_spinner_item, incidentList);
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

                    SharedPrefManager sharedPrefManager  = new SharedPrefManager(RegisterPetugas.this);
                    sharedPrefManager.saveSPString(SharedPrefManager.INCIDENT,Incident);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Tidak melakukan apa-apa jika tidak dipilih
            }
        });
    }

    // Method untuk memilih gambar
    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    // Method untuk memilih dokumen PDF
    private void pickDocument(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                if (requestCode == PICK_IMAGE_REQUEST_WAJAH) {
                    bitmapFotoWajah = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imgFotoWajah.setImageBitmap(bitmapFotoWajah);
                } else if (requestCode == PICK_IMAGE_REQUEST_KTP) {
                    bitmapFotoKTP = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imgFotoKTP.setImageBitmap(bitmapFotoKTP);
                } else if (requestCode == PICK_DOC_SURAT_IZIN) {
                    suratIzinBase64 = uriToBase64(uri);
                    tvFileSuratIzinPraktek.setText("File terpilih");
                } else if (requestCode == PICK_DOC_TANDA_REGISTRASI) {
                    tandaRegistrasiBase64 = uriToBase64(uri);
                    tvFileTandaRegistrasi.setText("File terpilih");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method untuk mengkonversi bitmap ke Base64
    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Method untuk mengkonversi file Uri ke Base64
    private String uriToBase64(Uri uri) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        // Menggunakan InputStream biasa tanpa var
        InputStream inputStream = getContentResolver().openInputStream(uri);

        if (inputStream != null) {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close(); // Tutup InputStream setelah selesai
        }

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }


    // Method untuk mengunggah data ke server
    private void uploadData() {
        String namaLengkap = etNamaLengkap.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String noHp = etNoHp.getText().toString().trim();
        String tanggalLahir = etTanggalLahir.getText().toString().trim();
        String alamatLengkap = etAlamat.getText().toString().trim();
        String nik = etNIK.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (namaLengkap.isEmpty() || username.isEmpty() || noHp.isEmpty() || tanggalLahir.isEmpty() || alamatLengkap.isEmpty() || nik.isEmpty() || password.isEmpty() || bitmapFotoWajah == null || bitmapFotoKTP == null) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengunggah data...");
        progressDialog.show();

        String fotoWajahBase64 = bitmapToString(bitmapFotoWajah);
        String fotoKTPBase64 = bitmapToString(bitmapFotoKTP);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_API,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    if (response.equalsIgnoreCase("Registrasi petugas berhasil!")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterPetugas.this);
                        dialog.setTitle("Pendaftaran Berhasil !");
                        dialog.setMessage("Pendaftaran anda telah berhasil dan sedang di tinjau, jika di setujui maka anda baru bisa login ke QR TEST");
                        dialog.show();
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(getApplicationContext(), LoginPetugas.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        });
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama_lengkap", namaLengkap);
                params.put("username", username);
                params.put("no_handphone", noHp);
                params.put("tanggal_lahir", tanggalLahir);
                params.put("alamat_lengkap", alamatLengkap);
                params.put("nomor_nik", nik);
                params.put("password_login", password);
                params.put("foto_wajah", fotoWajahBase64);
                params.put("foto_ktp", fotoKTPBase64);
                params.put("nomor_izin_praktek", etIzinPraktek.getText().toString().trim());
                params.put("nomor_tanda_registrasi", etTandaRegistrasi.getText().toString().trim());
                params.put("dokumen_surat_izin_praktek", suratIzinBase64 != null ? suratIzinBase64 : "");
                params.put("dokumen_tanda_registrasi", tandaRegistrasiBase64 != null ? tandaRegistrasiBase64 : "");
                params.put("layanan", "Dokter Umum");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                                Toast.makeText(RegisterPetugas.this, "Tidak ada data incident", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterPetugas.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterPetugas.this, "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}
