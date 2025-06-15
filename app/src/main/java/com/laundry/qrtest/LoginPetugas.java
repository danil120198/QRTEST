package com.laundry.qrtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.laundry.qrtest.Petugas.HomePetugas;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPetugas extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private static final String URL_LOGIN = Data.SERVER+"qr_test/login_petugas.php";  // Ganti dengan URL server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_petugas);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterUser = findViewById(R.id.tvLogin_petugas);

        tvRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterPetugas.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginPetugas.this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
//                startActivity(new Intent(getApplicationContext(),HomeUser.class));
                }
            }
        });
    }
    private void login(final String username, final String password) {
        ProgressDialog dialog = new ProgressDialog(LoginPetugas.this);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Tidak perlu parsing JSON Object jika response berupa string langsung
                        if (response.equalsIgnoreCase("Login berhasil!")) {
                            Toast.makeText(LoginPetugas.this, response, Toast.LENGTH_SHORT).show();

                            SharedPrefManager sharedPrefManager = new SharedPrefManager(LoginPetugas.this);
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN,true);
                            sharedPrefManager.saveSPString(SharedPrefManager.Status,"petugas");
                            sharedPrefManager.saveSPString(SharedPrefManager.Username,username);
                            // Redirect ke halaman Home sesuai role default
                            Intent intent = new Intent(LoginPetugas.this, HomePetugas.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginPetugas.this, response, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(LoginPetugas.this, "Gagal menghubungi server: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginError", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}