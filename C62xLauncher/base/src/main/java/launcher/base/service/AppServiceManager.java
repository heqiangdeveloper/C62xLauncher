package launcher.base.service;

import java.util.HashMap;
import java.util.Map;

public class AppServiceManager {
    private static final Map<String, Object> mServices = new HashMap<>();
    public static final String SERVICE_SUICIDE = "app.service.suicide";
    public static final String SERVICE_STATS = "app.service.stats";
    public static final String SERVICE_THEME = "app.service.theme";
    public static final String SERVICE_CAR = "app.service.car";
    public static final String SERVICE_PLATFORM = "app.service.platform";
    public static final String SERVICE_TENCENT_SDK = "app.tencent.sdk";
    public static Object getService(String serviceName) {
        return mServices.get(serviceName);
    }

    public static void addService(String serviceName, Object service) {
        mServices.put(serviceName, service);
    }
}
