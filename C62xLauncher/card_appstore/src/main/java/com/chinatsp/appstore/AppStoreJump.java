package com.chinatsp.appstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class AppStoreJump {
    public static void jumpAppMarket(String finalPackageName, Context context) {
        try {
            Uri uri = Uri.parse("market://details?id=" + finalPackageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context,"请确认已安装应用商店App", Toast.LENGTH_SHORT).show();
        }
    }
}
