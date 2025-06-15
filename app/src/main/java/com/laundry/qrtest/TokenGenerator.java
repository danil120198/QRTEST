package com.laundry.qrtest;

public class TokenGenerator {

    // Ganti dengan App ID dan App Certificate Anda
    private static final String APP_ID = "4c96eef3216940af9a7bdf6baa675fe1";
    private static final String APP_CERTIFICATE = "3908f2a513064e9fb3caa212d9d99ec4";

    // Saluran dan UID
//    private static final String CHANNEL_NAME = "qrtest";
    private static final int UID = 0; // UID untuk pengguna anonim, atau gunakan UID spesifik Anda

    // Durasi token
    private static final int EXPIRATION_TIME_IN_SECONDS = 3600; // 1 jam

    public static String generateToken(String channel) {
        try {
            AccessToken2 token = new AccessToken2();

            // App ID dan App Certificate
            token.appId = APP_ID;
            token.appCertificate = APP_CERTIFICATE;

            // Waktu kadaluarsa
            int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
            int expireTimestamp = currentTimestamp + EXPIRATION_TIME_IN_SECONDS;

            // Konfigurasi layanan RTC
            AccessToken2.ServiceRtc serviceRtc = new AccessToken2.ServiceRtc(channel, UID);
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, expireTimestamp);

            // Tambahkan layanan ke token
            token.addService(serviceRtc);

            // Generate token
            return token.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

