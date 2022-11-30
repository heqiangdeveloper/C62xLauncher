package com.chinatsp.widgetcards.home;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import launcher.base.utils.EasyLog;

public class CardScrollUtil {
    private static int divider;

    public static void setDivider(int divider) {
        CardScrollUtil.divider = divider;
    }

    public static void scroll(LinearLayoutManager layoutManager, int position) {
        if (layoutManager == null || position < 0 ) {
            return;
        }
        EasyLog.d("CardScrollUtil", "scroll...."+position);
        printStack();
        layoutManager.scrollToPositionWithOffset(position, -divider / 2);
    }
    private static void printStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            EasyLog.i("CardScrollUtil", "printStack: "+stackTrace[i]);
        }
    }
}
