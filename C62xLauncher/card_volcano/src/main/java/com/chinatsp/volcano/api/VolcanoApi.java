package com.chinatsp.volcano.api;

import com.oushang.radio.network.ApiBase;

public class VolcanoApi extends ApiBase {
    public static final String baseUrl = "https://api-vehicle.volcengine.com/";

    public VolcanoApi() {
        super(baseUrl);
    }
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
