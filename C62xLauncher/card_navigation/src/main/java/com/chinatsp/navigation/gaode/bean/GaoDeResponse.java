package com.chinatsp.navigation.gaode.bean;

public class GaoDeResponse<T> {
    private String requestCode;
    private String responseCode;
    private boolean needResponse;
    private int protocolId;
    private String versionName;
    private String requestAuthor;
    private String messageType;
    private int statusCode;
    private T data;

    public String getRequestCode() {
        return requestCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getRequestAuthor() {
        return requestAuthor;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getData() {
        return data;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setRequestAuthor(String requestAuthor) {
        this.requestAuthor = requestAuthor;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setData(T data) {
        this.data = data;
    }
}
