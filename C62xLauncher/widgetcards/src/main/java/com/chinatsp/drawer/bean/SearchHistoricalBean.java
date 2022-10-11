package com.chinatsp.drawer.bean;

import android.util.Log;

public class SearchHistoricalBean {
    private final String TAG = "LocationBean";
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void printLog(){
        Log.d(TAG,"content = " + content );
    }
}
