package com.laundry.qrtest;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
//
//import io.agora.rtc2.IRtcEngineEventHandler;
//import io.agora.rtc2.RtcEngine;
//import io.agora.rtc2.video.VideoCanvas;
//import io.agora.rtc2.video.VideoEncoderConfiguration;


public class Testing extends AppCompatActivity {

//    private static final String APP_ID = "8c48ad8a0d634051996a55afc3ed1e8e";
//    private static final String TOKEN = "007eJxTYFiYPY1ThqfGVmtf6XEW7ndMB75sn7B09RWF2A0mciJveD8oMFgkm1gkplgkGqSYGZsYmBpaWpolmpompiUbp6YYplqkzrpWkt4QyMgw6fJNFkYGCATxuRlKUotLnDMS8/JScxgYAN+PIjw=";
//    private static final String CHANNEL_NAME = "testChannel";
//
//    private RtcEngine mRtcEngine;
//    private SurfaceView mLocalView;
//    private SurfaceView mRemoteView;
//
//    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
//        @Override
//        public void onUserJoined(int uid, int elapsed) {
//            Log.d("Agora", "User joined: " + uid);
//            runOnUiThread(() -> setupRemoteVideo(uid));
//        }
//
//        @Override
//        public void onUserOffline(int uid, int reason) {
//            Log.d("Agora", "User offline: " + uid);
//            runOnUiThread(() -> removeRemoteVideo());
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

//        initAgoraEngine();
//        setupLocalVideo();
//        joinChannel();
    }

//    private void initAgoraEngine() {
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), APP_ID, mRtcEventHandler);
//        } catch (Exception e) {
//            Log.e("Agora", "RTC Engine initialization failed: " + e.getMessage());
//        }
//    }
//
//    private void setupLocalVideo() {
//        FrameLayout container = findViewById(R.id.local_video_view_container);
//
//        // Buat SurfaceView untuk tampilan video lokal
//        SurfaceView localView = new SurfaceView(this);
//        container.addView(localView);
//
//        // Set SurfaceView ke RtcEngine untuk video lokal
//        mRtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
//        mRtcEngine.enableVideo();
//        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
//                new VideoEncoderConfiguration.VideoDimensions(640, 360),
//                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
//                VideoEncoderConfiguration.STANDARD_BITRATE,
//                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
//        ));
//        mLocalView = localView;
//    }
//
//    private void setupRemoteVideo(int uid) {
//        FrameLayout container = findViewById(R.id.remote_video_view_container);
//
//        // Buat SurfaceView untuk tampilan video remote
//        SurfaceView remoteView = new SurfaceView(this);
//        container.addView(remoteView);
//
//        // Set SurfaceView ke RtcEngine untuk video remote
//        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//        mRemoteView = remoteView;
//    }
//
//    private void joinChannel() {
//        mRtcEngine.joinChannel(TOKEN, CHANNEL_NAME, null, 0);
//    }
//
//
//
//    private void removeRemoteVideo() {
//        FrameLayout container = findViewById(R.id.remote_video_view_container);
//        container.removeAllViews();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mRtcEngine != null) {
//            mRtcEngine.leaveChannel();
//            RtcEngine.destroy();
//            mRtcEngine = null;
//        }
//    }
}
