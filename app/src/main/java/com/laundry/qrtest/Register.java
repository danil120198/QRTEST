package com.laundry.qrtest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_FACE = 1;
    private static final int PICK_IMAGE_KTP = 2;
    private ImageView imgFotoWajah, imgFotoKTP;
    private Bitmap bitmapFotoWajah, bitmapFotoKTP;
    private TextInputEditText etNama, etUsername, etNoHP, etPassword;
    private MaterialButton btnUploadFotoWajah, btnUploadFotoKTP, btnRegister;

    // Deklarasi di luar onCreate()
    private static final int PICK_SURAT_IZIN = 1;
    private static final int PICK_TANDA_REGISTRASI = 2;

    private Uri suratIzinUri, tandaRegistrasiUri;

    private TextView tvFileSuratIzinPraktek, tvFileTandaRegistrasi;

    private final String URL_API = Data.SERVER+"qr_test/register_user.php"; // Ganti dengan URL API Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Inisialisasi komponen
        etNama = findViewById(R.id.etnama);
        etUsername = findViewById(R.id.etUsername);
        etNoHP = findViewById(R.id.etnohp);
        etPassword = findViewById(R.id.etPassword);
        imgFotoWajah = findViewById(R.id.imgFotoWajah);
        imgFotoKTP = findViewById(R.id.imgFotoKTP);
        btnUploadFotoWajah = findViewById(R.id.btnUploadFotoWajah);
        btnUploadFotoKTP = findViewById(R.id.btnUploadFotoKTP);
        btnRegister = findViewById(R.id.btnRegister);

//        MaterialButton btnUploadSuratIzinPraktek = findViewById(R.id.btnUploadSuratIzinPraktek);
//        MaterialButton btnUploadTandaRegistrasi = findViewById(R.id.btnUploadTandaRegistrasi);


        // Event untuk upload foto wajah
        btnUploadFotoWajah.setOnClickListener(v -> openGallery(PICK_IMAGE_FACE));

        // Event untuk upload foto KTP
        btnUploadFotoKTP.setOnClickListener(v -> openGallery(PICK_IMAGE_KTP));

        // Event untuk mengirim data
        btnRegister.setOnClickListener(v -> uploadData());
    }

    // Buka galeri
    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (requestCode == PICK_IMAGE_FACE) {
                    bitmapFotoWajah = bitmap;
                    imgFotoWajah.setImageBitmap(bitmap); // Tampilkan foto wajah
                } else if (requestCode == PICK_IMAGE_KTP) {
                    bitmapFotoKTP = bitmap;
                    imgFotoKTP.setImageBitmap(bitmap); // Tampilkan foto KTP
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Konversi Bitmap ke String Base64
    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Fungsi untuk mengunggah data ke server
    private void uploadData() {
        String nama = etNama.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String noHp = etNoHP.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nama.isEmpty() || username.isEmpty() || noHp.isEmpty() || password.isEmpty() || bitmapFotoWajah == null || bitmapFotoKTP == null) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Konversi gambar ke Base64
        String fotoWajahBase64 = bitmapToString(bitmapFotoWajah);
        String fotoKTPBase64 = bitmapToString(bitmapFotoKTP);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengunggah data...");
        progressDialog.show();

        String url = Data.SERVER+"qr_test/register_user.php";
        // Menggunakan Volley untuk request POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    if (response.equalsIgnoreCase("Registrasi berhasil!")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                        dialog.setTitle("Pendaftaran Berhasil !");
                        dialog.setMessage("Pendaftaran anda telah berhasil dan sedang di tinjau, jika di setujui maka anda baru bisa login ke QR TEST");
                        dialog.show();
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(getApplicationContext(), Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                        });
                    }
                },
                error -> Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("username", username);
                params.put("no_hp", noHp);
                params.put("password", password);
                params.put("foto_wajah", fotoWajahBase64);
                params.put("foto_ktp", fotoKTPBase64);
                return params;
            }
        };

        // Tambahkan request ke antrean Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private String fileToBase64(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
