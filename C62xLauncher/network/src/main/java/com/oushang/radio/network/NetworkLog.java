package com.oushang.radio.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkLog {
    private static boolean isDebug = true;

    private static final String PreTag = "OushangRadio-network:";

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(PreTag + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(PreTag + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(PreTag + tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(PreTag + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(PreTag + tag, msg);
        }
    }

    public static void v(Class mContext, String msg) {
        if (isDebug) {
            Log.v(mContext.getSimpleName(), msg);
        }
    }

    private static int showLength = 3999;

    /**
     * 分段打印出较长log文本
     *
     * @param logContent 打印文本
     * @param tag        打印log的标记
     */
    public static void info(String tag, String logContent) {
        if (!isDebug) {
            return;
        }
        if (logContent.length() > showLength) {
            String show = logContent.substring(0, showLength);
            i(tag, show);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length() - showLength) > showLength) {
                String partLog = logContent.substring(showLength, logContent.length());
                info(tag, partLog);
            } else {
                String printLog = logContent.substring(showLength, logContent.length());
                i(tag, printLog);
            }
        } else {
            e(tag, logContent);
        }
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
    public static void json(String tag, String msg) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, ""+line);
        }
        printLine(tag, false);
    }

}
