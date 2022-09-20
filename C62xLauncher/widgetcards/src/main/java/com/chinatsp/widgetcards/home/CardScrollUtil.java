package com.chinatsp.widgetcards.home;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CardScrollUtil {
    private static int divider;

    public static void setDivider(int divider) {
        CardScrollUtil.divider = divider;
    }

    public static void scroll(LinearLayoutManager layoutManager, int position) {

        if (layoutManager == null || position < 0 ) {
            return;
        }
        layoutManager.scrollToPositionWithOffset(position, -divider / 2);
    }
}
