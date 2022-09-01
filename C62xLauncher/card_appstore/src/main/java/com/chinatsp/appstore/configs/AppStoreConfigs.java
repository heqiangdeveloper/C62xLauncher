package com.chinatsp.appstore.configs;

public class AppStoreConfigs {
    public static final String APPSTORESP = "appstore_sp";
    public static final String TOKEN = "token";
    public static final String GETTOKENTIME = "get_token_time";
    public static final long EXPIRE_TIME = 172800000;//token过期时间，48小时 = 172800000ms

    //获取token，参考https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-References/agcapi-obtain_token-0000001158365043#section624414128367
    //获取token的地址
    public static final String TOKEN_URL = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    //获取广告的地址
    public static final String ADS_URL = "https://connect-api.cloud.huawei.com/api/mas/v1/dsp/open/query";
    public static final String GRANT_TYPE = "client_credentials";//固定传入“client_credentials”
    public static final String CLIENT_ID = "954293747045125632";
    public static final String CLIENT_SECRET = "EBAA7BF02ECB0E31F6213AA514A208D67B17D9AD930A99756B6B26517D652B64";

    public static final int ADCOUNT = 6;//请求数量，暂时是6
    public static final String SCENEID = "2001";//场景id
    public static final String SLOTID = "6Cc22KKwYrfvHfi246aIFM8aBrwISc5c";//展示位id
}
