package com.laundry.qrtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.FileDescriptor;
import java.io.IOException;

public class ScreenRecordService extends Service {
    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";
    private static final String CHANNEL_ID = "ScreenRecordChannel";

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private WindowManager windowManager;
    private Surface surface;
    private Uri videoUri;
    private int screenWidth, screenHeight, densityDpi;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getRealSize(size); // Use getRealSize() for accurate dimensions
        screenWidth = size.x;
        screenHeight = size.y;
        densityDpi = getResources().getDisplayMetrics().densityDpi;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                startForeground(1, createNotification());
                startScreenRecording(intent);
            } else if (ACTION_STOP.equals(action)) {
                stopScreenRecording();
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    private void startScreenRecording(Intent intent) {
        int resultCode = intent.getIntExtra("RESULT_CODE", -1);
        Intent dataIntent = intent.getParcelableExtra("DATA_INTENT");

        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (projectionManager != null && resultCode != -1 && dataIntent != null) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, dataIntent);

            if (mediaProjection == null) {
                Log.e("ScreenRecordService", "MediaProjection gagal dibuat!");
                return;
            }

            setupMediaRecorder();
            surface = mediaRecorder.getSurface();

            mediaProjection.createVirtualDisplay(
                    "ScreenRecord",
                    screenWidth, screenHeight, densityDpi, // Use densityDpi here
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    surface, null, null
            );

            mediaRecorder.start();
            Log.d("ScreenRecordService", "Mulai perekaman layar...");
        }
    }

    private void setupMediaRecorder() {
        videoUri = getVideoUri();
        if (videoUri == null) {
            Log.e("ScreenRecordService", "Gagal mendapatkan URI video!");
            return;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        // Use MediaRecorder.OutputFormat.MPEG_4 for better compatibility
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mediaRecorder.setVideoSize(screenWidth, screenHeight);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024); // Reduce bitrate slightly

        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(videoUri, "rw");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                mediaRecorder.setOutputFile(fd); // Set the FileDescriptor directly
            } else {
                Log.e("ScreenRecordService", "ParcelFileDescriptor null!");
                return; // Stop here if pfd is null
            }
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ScreenRecordService", "MediaRecorder prepare failed: " + e.getMessage());
            return; // Stop if prepare fails
        }
    }



    private void stopScreenRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (IllegalStateException e) {
                Log.e("ScreenRecordService", "Error stopping MediaRecorder: " + e.getMessage());
                Toast.makeText(this, "Gagal menghentikan perekaman. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        }

        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }

        if (videoUri != null) {
            // Scan file agar muncul di galeri
            MediaScannerConnection.scanFile(this,
                    new String[]{getRealPathFromURI(videoUri)},
                    new String[]{"video/mp4"},
                    null);

            Toast.makeText(this, "Perekaman selesai!\nVideo tersimpan di DCIM", Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(this, "Gagal menyimpan video!", Toast.LENGTH_LONG).show();
        }
    }



    private Uri getVideoUri() {
        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(MediaStore.Video.Media.DISPLAY_NAME, "screen_record_" + System.currentTimeMillis() + ".mp4");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/ScreenRecords");

        Uri videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        if (videoUri == null) {
            Log.e("ScreenRecordService", "Gagal membuat URI untuk video");
        }

        return videoUri;
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Video.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }







    private void addVideoToGallery(Uri videoUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(videoUri);
        sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScreenRecording();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Screen Recording",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Perekaman Layar")
                .setContentText("Aplikasi sedang merekam layar...")
                .setSmallIcon(R.drawable.nurse2) // Replace with your icon
                .setOngoing(true)
                .build();
    }
}