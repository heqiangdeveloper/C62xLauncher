
package com.chinatsp.vehiclesetting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chinatsp.vehiclesetting.VehicleService;


public class BootReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT_COMPLETED = "com.chinatsp.ACTION_CHINATSP_BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || ACTION_BOOT_COMPLETED.equals(action)) {
            Intent i = new Intent(context, VehicleService.class);
            context.startService(i);
        }
    }

}
