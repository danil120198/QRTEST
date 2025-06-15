package com.laundry.qrtest;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.senderName.setText( chat.getSenderId());
        holder.lastMessage.setText(chat.getContent());
        holder.time.setText(chat.getSentAt());

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        holder.lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("user_id",sharedPrefManager.getUsername())
                        .putExtra("sender_id",chat.getSenderId())
                        .putExtra("call_id",chat.getEmergencyCallId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, lastMessage, time;

        RelativeLayout lin1;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.tvSenderName);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            time = itemView.findViewById(R.id.tvMessageTime);
            lin1 = itemView.findViewById(R.id.lin);


        }


    }
}
