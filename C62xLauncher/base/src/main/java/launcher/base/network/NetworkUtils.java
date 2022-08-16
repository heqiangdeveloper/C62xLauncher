package launcher.base.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    /**
     * 网络是否连接和可用
     * @return true of false 是否有活动的网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        //获取连接活动管理器
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取链接网络信息
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(TAG, "isNetworkAvailable networkInfo:" + (networkInfo == null? "null": networkInfo.toString()));
        return (networkInfo != null && (networkInfo.isAvailable() || networkInfo.isConnected()));
    }
}
