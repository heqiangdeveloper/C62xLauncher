package launcher.base.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStateReceiver.class.getSimpleName();
    private static final String NETWORK_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private List<NetworkObserver> mNetworkObservers;
    private boolean isRegister = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (NETWORK_CHANGE_ACTION.equalsIgnoreCase(action)) {
            Log.d(TAG, "connectivity change");
            if (NetworkUtils.isNetworkAvailable(context)) {
                notifyObserver(true);
            } else {
                Log.e(TAG, "网络不可用");
                notifyObserver(false);
            }
        }
    }

    private void notifyObserver(boolean isConnected) {
        if (mNetworkObservers != null) {
            for (NetworkObserver observer : mNetworkObservers) {
                observer.onNetworkChanged(isConnected);
            }
        }
    }

    private NetworkStateReceiver() {
    }

    private static class NetWorkStateReceierHolder {
        private static NetworkStateReceiver HOLDER = new NetworkStateReceiver();
    }

    public static NetworkStateReceiver getInstance() {
        return NetWorkStateReceierHolder.HOLDER;
    }

    public void registerReceiver(Context context) {
        Log.d(TAG, "registerReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE_ACTION);
        context.registerReceiver(NetworkStateReceiver.getInstance(), filter);
        isRegister = true;
    }

    public void unRegisterReceiver(Context context) {
        Log.d(TAG, "unRegisterReceiver");
        if (isRegister) {
            context.unregisterReceiver(NetworkStateReceiver.getInstance());
            isRegister = false;
        }
    }

    public void registerObserver(NetworkObserver observer) {
        Log.d(TAG, "registerObserver");
        if (mNetworkObservers == null) {
            mNetworkObservers = new ArrayList<>();
        }
        mNetworkObservers.add(observer);
    }

    public void unRegisterObserver(NetworkObserver observer) {
        Log.d(TAG, "unRegisterObserver");
        if (mNetworkObservers != null && mNetworkObservers.contains(observer)) {
            mNetworkObservers.remove(observer);
        }
    }
}
