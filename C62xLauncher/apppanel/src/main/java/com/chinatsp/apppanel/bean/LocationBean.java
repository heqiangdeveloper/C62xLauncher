package com.chinatsp.apppanel.bean;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class LocationBean {
    private int parentIndex;
    private int childIndex;
    private String title;
    private String packageName;
    private byte[] imgByte;
    private String name;
    private int addBtn;
    private int status;
    private int priority;
    private int installed;
    private int canuninstalled;
    private Drawable imgDrawable;
    private final String TAG = "LocationBean";

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public byte[] getImgByte() {
        return imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddBtn() {
        return addBtn;
    }

    public void setAddBtn(int addBtn) {
        this.addBtn = addBtn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getInstalled() {
        return installed;
    }

    public void setInstalled(int installed) {
        this.installed = installed;
    }

    public int getCanuninstalled() {
        return canuninstalled;
    }

    public void setCanuninstalled(int canuninstalled) {
        this.canuninstalled = canuninstalled;
    }

    public Drawable getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public void printLog(){
        Log.d(TAG,"parentIndex = " + parentIndex + ",childIndex = " + childIndex + ",packageName = " +
                packageName + ",name = " + name);
    }
}
