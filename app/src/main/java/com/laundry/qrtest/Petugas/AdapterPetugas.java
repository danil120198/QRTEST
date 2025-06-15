package com.laundry.qrtest.Petugas;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.laundry.qrtest.Data;
import com.laundry.qrtest.HomeUser;
import com.laundry.qrtest.R;
import com.laundry.qrtest.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPetugas extends RecyclerView.Adapter<AdapterPetugas.ViewHolder> {

    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private List<DataRecyclerView> dataList;

    public AdapterPetugas(Context context, List<DataRecyclerView> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerView data = dataList.get(position);

        holder.tvIncidentTitle.setText(data.getUserId() + "\n" + data.getIncidentTitle());



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Double lat = Double.parseDouble(data.getLocationLat());
                            Double lng = Double.parseDouble(data.getLocationLong());
                            Double latnow = location.getLatitude();
                            Double lngnow = location.getLongitude();
                            float hasil = calculateDistance(latnow,lngnow,lat,lng);
                            // Format agar hanya 2 angka di belakang koma
                            DecimalFormat df = new DecimalFormat("#.##");
                            String formattedDistance = df.format(hasil);
                            holder.radius.setText(formattedDistance +" Km");

                        }
                    }
                });

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        holder.acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proses(data.getCallId(),sharedPrefManager.getUsername());
                context.startActivity(new Intent(context, HomeUser.class)
                        .putExtra("channel",data.getIncidentTitle())
                        .putExtra("token",data.getToken())
                        .putExtra("user_id",data.getUserId())
                        .putExtra("call_id",data.getCallId())
                        .putExtra("status","petugas"));
            }
        });

    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("Titik A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("Titik B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        // Hitung jarak dalam meter lalu konversi ke kilometer
        return locationA.distanceTo(locationB) / 1000;
    }


    private void proses(String call_id, String petugas_id) {
        String HttpURL = Data.SERVER + "qr_test/update_call_petugas.php";
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Loading... ");
//        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response
                        try {
                            if (response.endsWith("Data Berhasil Diupdate")) {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(), "Data berhasil dikirim", Toast.LENGTH_SHORT).show();
//                                finish();
                            } else {
//                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            // Handle exception
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
//                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                Map<String, String> params = new HashMap<>();
                params.put("call_id", call_id);
                params.put("petugas_id", petugas_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCallId, tvUserId,radius, tvPetugasId, tvIncidentTitle, tvToken, tvStatus, tvStartTime, tvEndTime, tvLocationLat, tvLocationLong, tvCreatedAt;

        ImageView acc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIncidentTitle = itemView.findViewById(R.id.title);
            radius = itemView.findViewById(R.id.radius);
            acc = itemView.findViewById(R.id.acc);
        }
    }
}

