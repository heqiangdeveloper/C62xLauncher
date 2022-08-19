package com.chinatsp.volcano.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import launcher.base.service.AppServiceManager;
import launcher.base.service.car.ICarService;
import launcher.base.utils.EasyLog;
import launcher.base.utils.security.Base64Util;
import launcher.base.utils.security.DigestUtils;

public class VolcanoApiParam {
    private static final String AK = "baicapiwhm0odypi7v94azb1c8exqlukst3fn";
    private static final String SK = "8kw60p9fxtbv72ro4znaqs3eicl15jyudhgm";

    private final String KEY_NONCE = "_nonce";
    private final String KEY_TIME_STAMP = "_timestamp";
    private final String KEY_VEHICLE_TYPE = "vehicle_type";
    private final String KEY_DEVICE_SN = "device_sn";

    private Map<String, String> mQueryParams = new HashMap<>();
    private String mQueryStr;
    private String mSign;
    private String mHttpMethod;

    public VolcanoApiParam(String httpMethod) {
        this.mHttpMethod = httpMethod;
        createCommonParams();
    }

    private void createCommonParams() {
        mQueryParams.put(KEY_NONCE, createNonce());
        mQueryParams.put(KEY_TIME_STAMP, String.valueOf(System.currentTimeMillis()/1000));
        ICarService carService = (ICarService) AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        mQueryParams.put(KEY_VEHICLE_TYPE, carService.getCarType());
        mQueryParams.put(KEY_DEVICE_SN, carService.getVinCode());
    }

    public void addQueryField(String Key, String value) {
        mQueryParams.put(Key, value);
    }

    private String createNonce() {
        Random random = new Random(System.currentTimeMillis());
        return String.valueOf(random.nextInt(65536));
    }

    private String createQueryStr(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        List<String> queryKeys = new LinkedList<>(map.keySet());
        Collections.sort(queryKeys);
        StringBuilder result = new StringBuilder();
        for (String queryKey : queryKeys) {
            result.append(queryKey).append("=").append(map.get(queryKey)).append("&");
        }
        int length = result.length();
        result.deleteCharAt(length - 1);
        return result.toString();
    }

    public String computeSign() {
        if (mQueryStr == null) {
            return "";
        }
        String sign = mHttpMethod + "\n" + mQueryStr + "\n";
        EasyLog.d("computeSign", "computeSign origin: "+sign);
        sign = DigestUtils.hmacSha256(SK, sign);
        EasyLog.d("computeSign", "computeSign hmacsha256: "+sign);
        sign = Base64Util.encodeHex(sign);
        EasyLog.d("computeSign", "computeSign base64: "+sign);
        return sign;
    }

    public Map<String, String> getQueryParams() {
        return mQueryParams;
    }

    public Map<String,String> getHeader() {
        Map<String, String> map = new HashMap<>();
        map.put("X-Signature", AK + ":" + mSign);
        map.put("X-Tt-Logid", "launcher_card_0818");
        map.put("X-Use-PPE", "1");
        map.put("X-Tt-Env", "ppe_001");
        return map;
    }

    public void compute() {
        mQueryStr = createQueryStr(mQueryParams);
        mSign = computeSign();
    }
}
