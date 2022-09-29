package com.anarchy.classifyview.Bean;

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
    private String reserve1;//应用的版本号
    private String reserve2;//不再提醒时的版本号
    private String reserve3;//有更新推送的版本号
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

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
    }

    public void printLog(){
        Log.d(TAG,"parentIndex = " + parentIndex + ",childIndex = " + childIndex + ",packageName = " +
                packageName + ",name = " + name);
    }
}
