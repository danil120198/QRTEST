package com.laundry.qrtest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PICK = 2;
    private static final int REQUEST_FILE_PICK = 3;
    private static final int REQUEST_KOORDINAT = 4;

    String messagetType = "";
    private String currentPhotoPath;
    private Uri photoURI;

    private EditText messageInput;
    private ImageButton sendButton, btnAttachment;
    private RecyclerView messageList;
    private MessageAdapter2 adapter;
    private List<Message> messages = new ArrayList<>();
    private String channelName = "chatroom"; // Default channel
    private Handler handler;
    private Runnable runnable;
    String lat="",lng="";
    TextView title;
    SharedPrefManager sharedPrefManager;
    private String fileBase64 = "", tandaRegistrasiBase64;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPrefManager = new SharedPrefManager(this);
        bundle = getIntent().getExtras();

        // Inisialisasi UI
        messageInput = findViewById(R.id.etMessage);
        sendButton = findViewById(R.id.btnSend);
        btnAttachment = findViewById(R.id.btnfile);
        messageList = findViewById(R.id.message_list);
        title = findViewById(R.id.tvLocationInfo);



        // Setup RecyclerView
        adapter = new MessageAdapter2(messages, this);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(adapter);

        // Tombol kirim pesan
        sendButton.setOnClickListener(v -> {
            if (sharedPrefManager.getStatus().equals("user")){

                    String messageText = messageInput.getText().toString();
                    if (TextUtils.isEmpty(messageText)) {
    //                Toast.makeText(this, "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    } else {
                        sendButton.setEnabled(false);
                        messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","",lat,lng)); // Jika hanya teks
                        adapter.notifyDataSetChanged();
                        messageList.scrollToPosition(messages.size() - 1);
                        sendMessage(bundle.getString("call_id")+"", sharedPrefManager.getUsername()+"", bundle.getString("sender_id")+"","text" , messageText, null); // Kirim teks tanpa file
                        messageInput.setText(""); // Reset input field


                    }
            }
            else {

                String messageText = messageInput.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    //                Toast.makeText(this, "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    sendButton.setEnabled(false);
                    messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","",lat,lng)); // Jika hanya teks
                    adapter.notifyDataSetChanged();
                    messageList.scrollToPosition(messages.size() - 1);
                    sendMessage(bundle.getString("call_id")+"", sharedPrefManager.getUsername()+"", bundle.getString("sender_id")+"","text" , messageText, null); // Kirim teks tanpa file
                    messageInput.setText(""); // Reset input field
                }
            }

        });

        // Tombol attachment
        btnAttachment.setOnClickListener(v -> showAttachmentDialog());

        if (bundle!=null){
            title.setText(bundle.getString("sender_id"));

            messageList.scrollToPosition(messages.size() - 1);
            fetchChatMessages(bundle.getString("sender_id"), sharedPrefManager.getUsername());
        }


        startAutoRefresh();

        requestPermissions();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1235);
        }
    }


    private void startAutoRefresh() {
        handler = new Handler();
        runnable = () -> {
//            messageList.scrollToPosition(messages.size() - 1);
            fetchChatMessages(bundle.getString("sender_id"), sharedPrefManager.getUsername());
            handler.postDelayed(runnable, 1000);
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void showAttachmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Aksi")
                .setItems(new CharSequence[]{"Ambil Foto", "Pilih dari Galeri","Kirim File","Kirim Lokasi"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    dispatchTakePictureIntent(); // Kamera
                                    break;
                                case 1:
                                    openGallery(); // Galeri
                                    break;
                                case 2:
                                    openFilePicker(); // File
                                    break;
                                case 3:
                                    openKoordinatPicker(); // File
                                    break;
                            }
                        })
                .show();
    }



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY_PICK);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_FILE_PICK);
    }
    private void openKoordinatPicker() {
        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_KOORDINAT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (photoURI != null) {
                        handleImageCapture(photoURI);
                    } else {
                        Toast.makeText(this, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_GALLERY_PICK:
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        handleGalleryPick(selectedImageUri);
                    } else {
                        Toast.makeText(this, "Gagal memilih gambar", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_FILE_PICK:
                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        try {
                            fileBase64 = uriToBase64(uri);
                            if (fileBase64.equals("")){

                            }else {
                                uploadMessageFile(
                                        bundle.getString("call_id", ""),
                                        sharedPrefManager.getUsername(),
                                        bundle.getString("sender_id", ""),
                                        "file",  // Message Type
                                        "",       // Message Text
                                        fileBase64,
                                        ""
                                );
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        Uri selectedFileUri = data.getData();
//                        handleFilePick(selectedFileUri);
                    } else {
                        Toast.makeText(this, "Gagal memilih file", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_KOORDINAT:
                        String result = data.getStringExtra("RESULT_TEXT");
                        String[] splitResult = result.split(",");
                         lat = splitResult[0];
                         lng = splitResult[1];
                    sendMessageKoordinat(bundle.getString("call_id")+"", sharedPrefManager.getUsername()+"", bundle.getString("sender_id")+"","location" , "", null); // Kirim teks tanpa file



                    break;
            }
        } else {
            Toast.makeText(this, "Aksi dibatalkan", Toast.LENGTH_SHORT).show();
        }
    }

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



    private void uploadMessageFile(String emergencyCallId, String senderId, String receiverId, String messageType, String messageText, String base64File, String fileName) {
        String url = Data.SERVER + "qr_test/upload_file.php";

        ProgressDialog dialog = new ProgressDialog(ChatActivity.this);
        dialog.setTitle("Mengupload File..");
        dialog.setMessage("Tunggu sebentar ya, tidak lama kok");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        dialog.dismiss();

                        Data.sendNotification(getApplicationContext(),"Pesan Baru",fileName,receiverId);
//                        Toast.makeText(this, status.equals("success") ? "Pesan berhasil dikirim: " + message : "Gagal mengirim pesan: " + message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> dialog.dismiss()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emergency_call_id", emergencyCallId);
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message_type", messageType);
                params.put("message", messageText);

                if (fileBase64 != null && !fileBase64.isEmpty()) {
                    params.put("file", fileBase64);
                    params.put("file_name", fileName);
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private boolean isUploading = false;

    private void uploadMessage(String emergencyCallId, String senderId, String receiverId, String messageType, String messageText, String base64File, String fileName) {
        // Cek apakah sudah dalam proses upload
        if (isUploading) {
            Toast.makeText(this, "Sedang mengupload, mohon tunggu...", Toast.LENGTH_SHORT).show();
            return;
        }

        isUploading = true;

        String url = Data.SERVER + "qr_test/upload_file.php";

        ProgressDialog dialog = new ProgressDialog(ChatActivity.this);
        dialog.setTitle("Mengupload File..");
        dialog.setMessage("Tunggu sebentar ya, tidak lama kok");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        dialog.dismiss();

                        isUploading = false;

                        Data.sendNotification(getApplicationContext(), "Pesan Baru", fileName, receiverId);
//                        Toast.makeText(this, status.equals("success") ? "Pesan berhasil dikirim: " + message : "Gagal mengirim pesan: " + message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        isUploading = false;
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    isUploading = false;
                    Toast.makeText(this, "Gagal mengupload file, coba lagi!", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emergency_call_id", emergencyCallId);
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message_type", messageType);
                params.put("message", messageText);

                if (base64File != null && !base64File.isEmpty()) {
                    params.put("file", base64File);
                    params.put("file_name", fileName);
                }
                return params;
            }
        };

        // Batasi pengulangan otomatis (timeout = 0, retry = 0)
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, // Timeout dalam milidetik (0 = tidak ada timeout)
                0, // Tidak ada pengulangan
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Beri tag untuk menghindari duplikasi
        stringRequest.setTag("UPLOAD");

        // Batalkan request dengan tag yang sama jika sudah ada
        MySingleton.getInstance(this).getRequestQueue().cancelAll("UPLOAD");

        // Tambahkan request ke Singleton
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private String encodeFileToBase64(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    private void sendMessage(String emergencyCallId, String senderId, String receiverId, String messageType, String messageText, File file) {
        String url = Data.SERVER + "qr_test/tambah_chat.php"; // Ganti dengan URL server Anda
//        if (file != null) {
//            messages.add(new Message("Anda", "","image",file.getName())); // Jika ada file
//        } else {
//            messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","")); // Jika hanya teks
//        }
//        // Konversi file ke base64 jika ada file
//        String fileBase64 = null;
//        if (file != null) {
//            fileBase64 = encodeFileToBase64(file);
//        }
//
//        // Buat permintaan dengan Volley
//        String finalFileBase6 = fileBase64;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Parsing respons dari server
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.getString("status").equals("success")) {
//                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();

                            sendButton.setEnabled(true);
                            Data.sendNotification(getApplicationContext(),sharedPrefManager.getUsername(),messageText,receiverId);
                            // Tambahkan pesan ke daftar RecyclerView
//                            if (file != null) {
//                                messages.add(new Message("Anda", "","image",file.getName())); // Jika ada file
//                            } else {
//                                messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","")); // Jika hanya teks
//                            }

                            // Gulir ke posisi terakhir

                        } else {

                            sendButton.setEnabled(true);
                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                        sendButton.setEnabled(true);
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log dan tampilkan error
                    Log.e("SendMessage", "Error: " + error.getMessage());

                    sendButton.setEnabled(true);
                    Toast.makeText(this, "Gagal mengirim pesan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emergency_call_id", emergencyCallId);
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message_type", messageType);
                params.put("message", messageText);

                // Sertakan file dalam format base64 jika ada
//                if (finalFileBase6 != null) {
//                    params.put("file", finalFileBase6);
                    params.put("file_name", "");
//                }

                // Debug log parameter yang dikirim
                Log.d("SendMessage", "Params: " + params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Tambahkan permintaan ke antrian Volley
        Volley.newRequestQueue(this).add(stringRequest);
    }




    private void sendMessageKoordinat(String emergencyCallId, String senderId, String receiverId, String messageType, String messageText, File file) {
        String url = Data.SERVER + "qr_test/tambah_chat.php"; // Ganti dengan URL server Anda
//        if (file != null) {
//            messages.add(new Message("Anda", "","image",file.getName())); // Jika ada file
//        } else {
//            messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","")); // Jika hanya teks
//        }
//        // Konversi file ke base64 jika ada file
//        String fileBase64 = null;
//        if (file != null) {
//            fileBase64 = encodeFileToBase64(file);
//        }
//
//        // Buat permintaan dengan Volley
//        String finalFileBase6 = fileBase64;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Parsing respons dari server
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(this,"Lokasi Terkirim", Toast.LENGTH_SHORT).show();

                            Data.sendNotification(getApplicationContext(),"Pesan Baru",messageText,receiverId);
                            // Tambahkan pesan ke daftar RecyclerView
//                            if (file != null) {
//                                messages.add(new Message("Anda", "","image",file.getName())); // Jika ada file
//                            } else {
//                                messages.add(new Message(sharedPrefManager.getUsername(), messageText,"text","")); // Jika hanya teks
//                            }

                            // Gulir ke posisi terakhir

                        } else {
                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log dan tampilkan error
                    Log.e("SendMessage", "Error: " + error.getMessage());
                    Toast.makeText(this, "Gagal mengirim pesan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emergency_call_id", emergencyCallId);
                params.put("sender_id", senderId);
                params.put("receiver_id", receiverId);
                params.put("message_type", messageType);
                params.put("message", messageText);
                params.put("lat", lat);
                params.put("lng", lng);

                // Sertakan file dalam format base64 jika ada
//                if (finalFileBase6 != null) {
//                    params.put("file", finalFileBase6);
                params.put("file_name", "");
//                }

                // Debug log parameter yang dikirim
                Log.d("SendMessage", "Params: " + params.toString());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Tambahkan permintaan ke antrian Volley
        Volley.newRequestQueue(this).add(stringRequest);
    }










    private byte[] getFileDataFromFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void fetchChatMessages(String senderId, String receiverId) {
        String url = Data.SERVER + "qr_test/get_chats_live.php?sender_id=" + senderId + "&receiver_id=" + receiverId; // Ganti dengan URL API Anda

        // Buat permintaan menggunakan Volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parsing respons JSON dari server
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray chatArray = jsonResponse.getJSONArray("result");

                        // Bersihkan data lama di RecyclerView
                        messages.clear();

                        // Iterasi data chat
                        for (int i = 0; i < chatArray.length(); i++) {
                            JSONObject chatObject = chatArray.getJSONObject(i);

                            String messageId = chatObject.getString("message_id");
                            String emergencyCallId = chatObject.getString("emergency_call_id");
                            String senderIdFromServer = chatObject.getString("sender_id");
                            String receiverIdFromServer = chatObject.getString("receiver_id");
                            String messageType = chatObject.getString("message_type");
                            String content = chatObject.getString("content");
                            String filePath = chatObject.optString("file_path", null);
                            String sentAt = chatObject.getString("sent_at");
                            String latitude = chatObject.getString("latitude");
                            String longitude = chatObject.getString("longitude");

                            // Tambahkan data ke daftar
                            messages.add(new Message(
                                    senderIdFromServer,
                                    content,messageType,filePath,latitude,longitude
                            ));


                        }

                        // Beritahu adapter bahwa data telah berubah
                        adapter.notifyDataSetChanged();

                            messageList.scrollToPosition(messages.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Tangani error saat memanggil API
                    Log.e("FetchChatMessages", "Error: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch chat messages", Toast.LENGTH_SHORT).show();
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Tambahkan permintaan ke antrian Volley
        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            photoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void handleImageCapture(Uri imageUri) {
        try {
            // Konversi URI ke path fisik
            String filePath = getRealPathFromURI(imageUri);

            if (filePath != null) {
                File file = new File(filePath);

                // Konversi ke Base64
                String fileBase64 = encodeFileToBase64(file);
                String fileName = file.getName();

                // Kirim ke server
                uploadMessage(
                        bundle.getString("call_id"),
                        sharedPrefManager.getUsername(),
                        bundle.getString("sender_id", ""),
                        "image",  // Message Type
                        "",       // Message Text
                        fileBase64,
                        fileName
                );
            } else {
                Toast.makeText(this, "Gagal mendapatkan path gambar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menangani gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void handleGalleryPick(Uri galleryUri) {
        try {
            String filePath = getRealPathFromURI(galleryUri);
            if (filePath != null) {
                File file = new File(filePath);

                // Konversi ke Base64
                String fileBase64 = encodeFileToBase64(file);
                String fileName = file.getName();

                // Kirim ke server
                uploadMessage(
                        bundle.getString("call_id", ""),
                        sharedPrefManager.getUsername(),
                        bundle.getString("sender_id", ""),
                        "image",  // Message Type
                        "",       // Message Text
                        fileBase64,
                        fileName
                );
            } else {
                Toast.makeText(this, "Gagal mendapatkan path gambar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menangani gambar dari galeri: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void handleFilePick(Uri fileUri) {
        try {
            String filePath = getRealPathFromURI(fileUri);
            if (filePath != null) {
                File file = new File(filePath);

                // Konversi ke Base64
                String fileBase64 = encodeFileToBase64(file);
                String fileName = file.getName();

                // Kirim ke server
                uploadMessage(
                        bundle.getString("call_id", ""),
                        sharedPrefManager.getUsername(),
                        bundle.getString("sender_id", ""),
                        "file",  // Message Type
                        "",       // Message Text
                        fileBase64,
                        fileName
                );
            } else {
                Toast.makeText(this, "Gagal mendapatkan path file", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menangani file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String getRealPathFromURI(Uri uri) {
        String filePath = null;

        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            String[] split = documentId.split(":");
            String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            String selection = "_id=?";
            String[] selectionArgs = new String[]{split[1]};
            filePath = getDataColumn(contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getDataColumn(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }




}
