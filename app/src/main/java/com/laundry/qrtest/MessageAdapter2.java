package com.laundry.qrtest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter2 extends RecyclerView.Adapter<MessageAdapter2.MessageViewHolder> {

    private final List<Message> messages;
    private final Context context;

    private static final int VIEW_TYPE_LEFT = 0;  // Pesan dari orang lain
    private static final int VIEW_TYPE_RIGHT = 1; // Pesan milik sendiri

    public MessageAdapter2(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);

        // Tentukan apakah pesan milik pengguna sendiri atau orang lain
        return message.getUsername().equals(sharedPrefManager.getUsername()) ? VIEW_TYPE_RIGHT : VIEW_TYPE_LEFT;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);

        // Tampilkan nama pengguna
        if (message.getUsername().equals(sharedPrefManager.getUsername())) {
            holder.username.setText("Anda");
        } else {
            holder.username.setText(message.getUsername());
        }
        holder.messageText.setText(message.getText());

        holder.messageText.setTextColor(context.getColor(R.color.black));

        holder.messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.image.setVisibility(View.GONE);
        if (message.getFilepath().equals("")){
            if (message.getType().equals("location")){
                holder.messageText.setText("Koordinat :\n"+message.getLat()+","+message.getLng());
                holder.messageText.setTextColor(context.getColor(R.color.biru));
                holder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context,MapsActivity2.class).putExtra("lat",message.getLat()).putExtra("lng",message.getLng()));
                    }
                });
            }
        }else {

            if (message.getType().equals("file")){
                holder.messageText.setText("File :\n"+message.getFilepath());
                holder.messageText.setTextColor(context.getColor(R.color.biru));
                holder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context,PdfViewer.class).putExtra("file",message.getFilepath()));
                    }
                });
            } else if (message.getType().equals("image")){
                Glide.with(context).load(Data.SERVER+"qr_test/uploads/"+message.getFilepath()).centerCrop().into(holder.image);
                holder.image.setVisibility(View.VISIBLE);
            }else if (message.getType().equals("location")){
                holder.messageText.setText(message.getLat()+","+message.getLng());
                holder.messageText.setTextColor(context.getColor(R.color.biru));
                holder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context,MapsActivity2.class).putExtra("lat",message.getLat()).putExtra("lng",message.getLng()));
                    }
                });
            }else {

            }

        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageDialog(context, Data.SERVER+"qr_test/uploads/"+message.getFilepath()).show();
            }
        });

//        // Atur teks berdasarkan tipe pesan
//        switch (message.getType()) {
//            case "text":
//                holder.messageText.setText(message.getText());
//                holder.messageText.setOnClickListener(v -> {
//                    // Tampilkan pesan teks
//                    Toast.makeText(v.getContext(), "Pesan teks: " + message.getText(), Toast.LENGTH_SHORT).show();
//                });
//                break;
//
//            case "image":
//                holder.messageText.setText("Lihat Gambar");
//                holder.messageText.setOnClickListener(v -> {
//                    // Buka gambar menggunakan Intent
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(message.getFilepath()), "image/*");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    try {
//                        v.getContext().startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        Toast.makeText(v.getContext(), "Tidak ada aplikasi untuk membuka gambar", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//
//            default:
//                holder.messageText.setText("Tipe pesan tidak dikenali");
//                break;
//        }

        // Atur avatar pengguna
        holder.userIcon.setImageResource(R.drawable.avatar1); // Default avatar
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView username, messageText;
        ImageView userIcon,image;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            messageText = itemView.findViewById(R.id.message_text);
            userIcon = itemView.findViewById(R.id.user_icon);
            image = itemView.findViewById(R.id.image);
        }
    }
}
