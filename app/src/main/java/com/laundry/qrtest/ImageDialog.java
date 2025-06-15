package com.laundry.qrtest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

public class ImageDialog extends Dialog {

    private final String imageUrl; // URL gambar

    public ImageDialog(@NonNull Context context, String imageUrl) {
        super(context);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_full_image);

        ImageView imageView = findViewById(R.id.full_image_view);
        ImageView closeButton = findViewById(R.id.close_button);

        // Muat gambar menggunakan Glide
        Glide.with(getContext())
                .load(Uri.parse(imageUrl))
                .into(imageView);

        closeButton.setOnClickListener(v -> dismiss());
    }
}
