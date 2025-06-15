package com.laundry.qrtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by qwerty on 1/25/2018.
 */

public class Data {

    static ProgressDialog pDialog;
//    public static final String SERVER = "http://172.20.10.4/";
//      public static final String SERVER = "http://10.0.2.2/";
//    public static final String SERVER = "http://192.168.100.4/";
//    public static final String SERVER = "http://develovit.com/";
    public static final String SERVER = "https://fastmedas.com/";


    public static void sendNotification(Context context, String title, String body, String topic) {
        // URL target dengan parameter
        String url = Data.SERVER+"qr_test/send_notification.php"
                + "?title=" + title
                + "&body=" + body
                + "&topic=" + topic;

        // Ganti "localhost" dengan IP komputer Anda jika menjalankan pada perangkat fisik
//        url = url.replace("localhost", "192.168.1.x"); // Sesuaikan IP Address Anda

        // Inisialisasi RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Membuat StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Tampilkan respons dari server
//                        Log.d(TAG, "Response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log jika ada error
//                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                });

        // Menambahkan request ke antrian

        requestQueue.add(stringRequest);
    }

}
