package com.laundry.qrtest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AccessToken2 {

    public String appId;
    public String appCertificate;
    public int issueTimestamp;
    public int expireTimestamp;
    public Map<String, Service> services = new HashMap<>();

    public static final int VERSION = 2;

    public interface PrivilegeRtc {
        int PRIVILEGE_JOIN_CHANNEL = 1;
        int PRIVILEGE_PUBLISH_AUDIO_STREAM = 2;
        int PRIVILEGE_PUBLISH_VIDEO_STREAM = 3;
        int PRIVILEGE_PUBLISH_DATA_STREAM = 4;
    }

    public static class Service {
        public String serviceType;
        public Map<Integer, Integer> privileges = new HashMap<>();

        public byte[] pack() {
            StringBuilder privilegeBuffer = new StringBuilder();
            privileges.forEach((k, v) -> privilegeBuffer.append(k).append(':').append(v).append(';'));
            return privilegeBuffer.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    public static class ServiceRtc extends Service {
        public String channelName;
        public int uid;

        public ServiceRtc(String channelName, int uid) {
            this.serviceType = "rtc";
            this.channelName = channelName;
            this.uid = uid;
        }

        public void addPrivilegeRtc(int privilege, int expireTimestamp) {
            privileges.put(privilege, expireTimestamp);
        }

        @Override
        public byte[] pack() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(channelName).append(',').append(uid).append(',');
            privileges.forEach((k, v) -> buffer.append(k).append(':').append(v).append(';'));
            return buffer.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    public AccessToken2() {
        this.issueTimestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public void addService(Service service) {
        services.put(service.serviceType, service);
    }

    public String build() throws Exception {
        if (appId == null || appCertificate == null) {
            throw new Exception("App ID or App Certificate is null");
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(VERSION).append(':').append(appId).append(':');

        String signature = generateSignature();
        buffer.append(signature).append(':');

        byte[] content = packContent();
        buffer.append(new String(content, StandardCharsets.UTF_8));

        return buffer.toString();
    }

    private String generateSignature() throws NoSuchAlgorithmException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(appCertificate.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] content = packContent();
            byte[] digest = mac.doFinal(content);
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new NoSuchAlgorithmException("Failed to generate HMAC signature", e);
        }
    }

    private byte[] packContent() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(issueTimestamp).append(',').append(expireTimestamp).append(',');

        services.forEach((k, v) -> {
            buffer.append(k).append(':');
            buffer.append(new String(v.pack(), StandardCharsets.UTF_8)).append(';');
        });

        return buffer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
