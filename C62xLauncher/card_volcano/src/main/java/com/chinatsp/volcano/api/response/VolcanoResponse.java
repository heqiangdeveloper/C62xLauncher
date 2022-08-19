package com.chinatsp.volcano.api.response;

public class VolcanoResponse {

    public static final int CODE_SUCCESS = 0;

    private int errno;
    private String msg;
    private VideoListData data;


    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VideoListData getData() {
        return data;
    }

    public void setData(VideoListData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "VolcanoResponse{" +
                "errno=" + errno +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
