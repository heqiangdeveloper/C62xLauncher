package com.oushang.radio.network.errorhandler;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ProjectName: OnlineRadioService
 * @Package: com.edog.car.ximalaadapeter.beans.error
 * @ClassName: ErrorResponse
 * @Description: 描述说明：response异常信息
 * @Author: xuyuanlin
 * @Email: yuanlin.xu@faurcia.com
 * @CreateDate: 2021/3/5 14:27
 * @UpdateUser: xuyuanlin
 * @UpdateDate: 2021/3/5 14:27
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ErrorResponse implements Parcelable {
    /**
     * 5000	其它错误
     * 5001	thrift服务调用异常
     * 5002	http服务调用异常
     * 5003	权限验证错误
     * 5004	某资源未发现引发的错误
     * 5005	业务限制导致错误
     * 5006	程序运行时产生的错误（比如操作数据有误导致出错）
     */
    /**
     *	毫秒时间错
     */
    private long timestamp;
    /**
     *	http 错误状态代码
     */
    private int status;
    /**
     *	业务错误代码
     */
    private String error;
    /**
     *	错误描述信息
     */
    private String message;
    /**
     *	访问路径
     */
    private String path;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeInt(this.status);
        dest.writeString(this.error);
        dest.writeString(this.message);
        dest.writeString(this.path);
    }

    public ErrorResponse() {
    }

    protected ErrorResponse(Parcel in) {
        this.timestamp = in.readLong();
        this.status = in.readInt();
        this.error = in.readString();
        this.message = in.readString();
        this.path = in.readString();
    }

    public static final Creator<ErrorResponse> CREATOR = new Creator<ErrorResponse>() {
        @Override
        public ErrorResponse createFromParcel(Parcel source) {
            return new ErrorResponse(source);
        }

        @Override
        public ErrorResponse[] newArray(int size) {
            return new ErrorResponse[size];
        }
    };
}
