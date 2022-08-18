package com.chinatsp.volcano.api;

import android.os.SystemClock;

import java.lang.reflect.Constructor;
import java.util.Random;

public class VolcanoApiParam {



    private static class CommonParam {
        private int nonce;
        private long timeStamp;
        private String vehicle_type;
        private String device_id;
        public CommonParam() {
            Random random = new Random(System.currentTimeMillis());
            nonce = random.nextInt(65536);
            timeStamp = System.currentTimeMillis();
        }

        public String getQueryStr() {
            return "_nonce=" + nonce + "&_timestamp=" + timeStamp;
        }
    }

    public void create(CommonParam commonParam, String source) {
        String queryStr = commonParam.getQueryStr() + "&source=" + source;
    }


}
