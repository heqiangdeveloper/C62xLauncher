package com.common.xui.utils;


public class DataUtils {
    public static int[] dataFlashback(int[] value) {
        int[] date = new int[value.length];
        int foot = value.length - 1;
        for (int i = 0; i < date.length; i++) {
            date[i] = value[foot];
            foot--;
        }
        return date;
    }
}
