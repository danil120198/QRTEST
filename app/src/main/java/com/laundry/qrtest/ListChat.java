package com.laundry.qrtest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListChat extends AppCompatActivity {

    private RecyclerView recyclerChatList;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    private Handler handler;
    private Runnable runnable;
    private static final int REFRESH_INTERVAL = 5000; // Refresh setiap 5 detik
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        sharedPrefManager = new SharedPrefManager(this);
        recyclerChatList = findViewById(R.id.recyclerChatList);
        recyclerChatList.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerChatList.setAdapter(chatAdapter);

        fetchChatData(sharedPrefManager.getUsername());


        // Memulai refresh otomatis
        startAutoRefresh(sharedPrefManager.getUsername());
    }

    private void startAutoRefresh(String receiverId) {
        handler = new Handler();
        runnable = () -> {
            fetchChatData(receiverId);
            handler.postDelayed(runnable, REFRESH_INTERVAL);
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

    private void fetchChatData(String receiverId) {
        String url = Data.SERVER + "qr_test/get_chats.php?receiver_id=" + receiverId; // URL API Anda

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parsing hasil JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray chats = jsonResponse.getJSONArray("result");

                        // Tampilkan atau sembunyikan gambar jika data kosong
                        ImageView back = findViewById(R.id.back);
                        if (chats.length() == 0) {
                            back.setVisibility(View.VISIBLE);
                        } else {
                            back.setVisibility(View.GONE);
                        }

                        // Bersihkan daftar sebelum diisi ulang
                        chatList.clear();

                        // Gunakan HashMap untuk menyimpan hanya chat terbaru per sender_id
                        Map<String, Chat> uniqueChats = new HashMap<>();

                        for (int i = 0; i < chats.length(); i++) {
                            JSONObject chat = chats.getJSONObject(i);
                            String messageId = chat.getString("message_id");
                            String emergencyCallId = chat.getString("emergency_call_id");
                            String senderId = chat.getString("sender_id");
                            String receiverIdFromServer = chat.getString("receiver_id");
                            String messageType = chat.getString("message_type");
                            String content = chat.optString("content", ""); // Opsional jika null
                            String filePath = chat.optString("file_path", ""); // Opsional jika null
                            String latitude = chat.optString("latitude", ""); // Opsional jika null
                            String longitude = chat.optString("longitude", ""); // Opsional jika null
                            String sentAt = chat.getString("sent_at");

                            // Buat objek Chat
                            Chat chatObj = new Chat(messageId, emergencyCallId, senderId, receiverIdFromServer,
                                    messageType, content, filePath, latitude, longitude, sentAt);

                            // Simpan hanya chat terbaru per sender_id
                            if (!uniqueChats.containsKey(senderId) || isLater(sentAt, uniqueChats.get(senderId).getSentAt())) {
                                uniqueChats.put(senderId, chatObj);
                            }
                        }

                        // Tambahkan semua chat unik ke chatList
                        chatList.addAll(uniqueChats.values());

                        // Perbarui tampilan RecyclerView
                        chatAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        // Tambahkan permintaan ke antrian Volley
        Volley.newRequestQueue(this).add(request);
    }

    // Fungsi untuk membandingkan waktu pengiriman
    private boolean isLater(String time1, String time2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1 != null && date2 != null && date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }



}
