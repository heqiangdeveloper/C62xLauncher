package com.chinatsp.iquting.utils;

public class ToolUtils {
    /**
     * 转换时间格式为xx小时xx分xx秒
     * @param sencond 秒数
     */
    public static String formatTime(long sencond) {
        if (sencond <= 0) {
            return "00:00";
        }
        final StringBuffer sb = new StringBuffer();
        final long h = sencond / (60 * 60 );
        if ((h < 100) && (h > 0)) {
            sb.append(h < 10 ? "0" + h : h);
            sb.append(":");
        }

        final long m = (sencond % (60 * 60 )) / 60;
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        final long s = sencond % 60;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }
}
