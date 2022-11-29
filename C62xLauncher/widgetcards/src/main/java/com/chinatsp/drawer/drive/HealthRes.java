package com.chinatsp.drawer.drive;

import android.content.Context;

import com.chinatsp.widgetcards.R;

import java.util.Locale;

public class HealthRes {
    int textDrawableId;
    int bottomDrawableId;

    public HealthRes(int textDrawableId, int bottomDrawableId) {
        this.textDrawableId = textDrawableId;
        this.bottomDrawableId = bottomDrawableId;
    }

    public HealthRes() {

    }

    public static HealthRes getHealthRes(String healthLevel, Context context) {
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        boolean cn = locale.getLanguage().equals(Locale.CHINESE.getLanguage());
        return getHealthResByLang(healthLevel, cn);
    }

    private static HealthRes getHealthResByLang(String healthLevel, boolean cn) {
        int txtResId = cn ? R.drawable.drawer_drive_health_a : R.drawable.drawer_drive_health_a_en;
        int bottomResId = R.drawable.drawer_drive_health_bottom_a;
        if (healthLevel == null || healthLevel.isEmpty()) {
            return new HealthRes(txtResId, bottomResId);
        }
        HealthRes res = new HealthRes();
        if (healthLevel.equals("优")) {
            res.textDrawableId = cn ? R.drawable.drawer_drive_health_a : R.drawable.drawer_drive_health_a_en;
            res.bottomDrawableId = R.drawable.drawer_drive_health_bottom_a;
        } else if (healthLevel.equals("良")) {
            res.textDrawableId = cn ? R.drawable.drawer_drive_health_b : R.drawable.drawer_drive_health_b_en;
            res.bottomDrawableId = R.drawable.drawer_drive_health_bottom_b;
        } else if (healthLevel.equals("中")) {
            res.textDrawableId = cn ? R.drawable.drawer_drive_health_c : R.drawable.drawer_drive_health_c_en;
            res.bottomDrawableId = R.drawable.drawer_drive_health_bottom_c;
        } else if (healthLevel.equals("差")) {
            res.textDrawableId = cn ? R.drawable.drawer_drive_health_d : R.drawable.drawer_drive_health_d_en;
            res.bottomDrawableId = R.drawable.drawer_drive_health_bottom_d;
        } else {
            res.textDrawableId = txtResId;
            res.bottomDrawableId = bottomResId;
        }
        return res;
    }
}
