package com.oushang.radio.network;


import java.util.HashMap;

/**
 * @description
 * @author xuyuanli
 * @time 2021/2/2 15:02
 * @UpdateUser:     xuyuanli
 * @UpdateDate:    2021/2/2 15:02
 * @UpdateRemark:
 */
public interface INetworkRequestInfo {
    HashMap<String, String> getRequestHeaderMap();
    void addHeardMap(String key,String value);
    void removeHeardMap(String key);
    boolean isDebug();
}
