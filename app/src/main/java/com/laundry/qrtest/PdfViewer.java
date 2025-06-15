package com.laundry.qrtest;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PdfViewer extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_viewer);
        webView = findViewById(R.id.webView);

        // Aktifkan JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Bundle bundle = getIntent().getExtras();

        // Buka PDF dengan Google Docs Viewer
        String pdfUrl = Data.SERVER+"qr_test/uploads/"+bundle.getString("file"); // Ganti dengan URL PDF Anda
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
    }
}