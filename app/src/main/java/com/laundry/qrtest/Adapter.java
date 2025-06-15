package com.laundry.qrtest;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.qrtest.Petugas.CallActivityPetugas;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private final List<String> dataList;
   public Activity activity;

    // Constructor untuk menerima data
    public Adapter(Activity activity,List<String> dataList) {
        this.dataList = dataList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_call, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.textViewItem.setText(data);

        holder.acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.startActivity(new Intent(activity, CallActivityPetugas.class));
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        ImageView acc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.title);
            acc = itemView.findViewById(R.id.acc);



        }
    }
}
