package com.laundry.qrtest;

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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.username.setText(message.getUsername());

        String messageText = message.getText();
        if (messageText.contains("file:") || messageText.contains("content:")) {
            // Tampilkan sebagai file/gambar
            holder.messageText.setText("📁 Klik untuk melihat file");
            holder.messageText.setOnClickListener(v -> {
                Uri fileUri = Uri.parse(messageText.substring(messageText.indexOf(":") + 1));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Tidak dapat membuka file", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Tampilkan sebagai pesan teks
            holder.messageText.setText(messageText);
            holder.messageText.setOnClickListener(null);
        }

        // Set avatar default untuk semua user
        holder.userIcon.setImageResource(R.drawable.avatar1);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView username, messageText;
        ImageView userIcon;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            messageText = itemView.findViewById(R.id.message_text);
            userIcon = itemView.findViewById(R.id.user_icon);
        }
    }
}

