package com.laundry.qrtest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

public class AppUtils {
    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        // Hentikan proses aplikasi
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            am.killBackgroundProcesses(context.getPackageName());
        }

        // Matikan aplikasi
        System.exit(0);
    }
}
