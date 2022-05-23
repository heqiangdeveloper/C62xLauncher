package com.chinatsp.vehicle.settings.bean;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Result<T> {

    @SerializedName("error_code")
    private int code;

    private String message;

    @SerializedName("result")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message=" + message +
                ", data=" + data +
                '}';
    }
}
