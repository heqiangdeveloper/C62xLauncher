package com.chinatsp.settinglib;

import android.content.Context;
import android.content.Intent;

public class SettingsTool {

    private static boolean mEraseSdCard = false;
    private static boolean mEraseEsims = false;

    public static void doMasterClear(Context context,boolean eraseData) {
        Intent intent = new Intent(Intent.ACTION_FACTORY_RESET);
        intent.setPackage("android");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
        intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, eraseData);
        intent.putExtra(Intent.EXTRA_WIPE_ESIMS, eraseData);//eSim
        context.sendBroadcast(intent);
        // Intent handling is asynchronous -- assume it will happen soon.
    }
}
