package launcher.base.ipc;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.LinkedList;

import launcher.base.R;
import launcher.base.async.AsyncSchedule;
import launcher.base.utils.EasyLog;

public class BaseRemoteConnector {

    private static final String TAG = BaseRemoteConnector.class.getSimpleName();
    private volatile boolean startBindService = false;
    private volatile boolean mServiceConnect;
    private LinkedList<IConnectListener> mConnectListeners = new LinkedList<>();
    private LinkedList<IRemoteDataCallback> mRemoteDataCallbacks = new LinkedList<>();
    private RemoteProxy mRemoteProxy;

    public BaseRemoteConnector(@NonNull RemoteProxy remoteProxy) {
        mRemoteProxy = remoteProxy;
        mRemoteProxy.setConnectListener(createConnectListener());
        mRemoteProxy.setRemoteDataCallback(createRemoteDataCallback());
    }


    private IConnectListener createConnectListener() {
        return new IConnectListener() {
            @Override
            public void onServiceConnected() {
                mServiceConnect = true;
                notifyConnectChange(mServiceConnect);
            }

            @Override
            public void onServiceDisconnected() {
                mServiceConnect = false;
                notifyConnectChange(mServiceConnect);
            }
        };
    }

    private IRemoteDataCallback createRemoteDataCallback() {
        return new IRemoteDataCallback() {
            @Override
            public <T> void notifyData(T t) {
                AsyncSchedule.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataCallback(t);
                    }
                });
            }
        };
    }

    private <T> void notifyDataCallback(T t) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                for (IRemoteDataCallback remoteDataCallback : mRemoteDataCallbacks) {
                    remoteDataCallback.notifyData(t);
                }
            }
        });
    }

    private void notifyConnectChange(boolean status) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                if (status) {
                    for (IConnectListener connectListener : mConnectListeners) {
                        connectListener.onServiceConnected();
                    }
                } else {
                    for (IConnectListener connectListener : mConnectListeners) {
                        connectListener.onServiceDisconnected();
                    }
                }
            }
        });
    }


    public synchronized void bindServiceAsync(Context context) {
        if (startBindService) {
            return;
        }
        startBindService = true;
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                bindService(context);
            }
        });
    }

    private void bindService(Context context) {
        EasyLog.i(TAG, "start: bindService");
        if (mServiceConnect) {
            EasyLog.w(TAG, "start: bindService cancel : already connected.");
            return;
        }
        mRemoteProxy.connectRemoteService(context);
    }

    public void requestData(IOnRequestListener onRequestListener) {
        mRemoteProxy.requestData(onRequestListener);
    }

    public synchronized void registerConnectListener(IConnectListener connectListener) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                if (connectListener != null) {
                    mConnectListeners.add(connectListener);
                }
            }
        });

    }

    public synchronized void unregisterConnectListener(IConnectListener connectListener) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                if (connectListener != null) {
                    mConnectListeners.remove(connectListener);
                }
            }
        });
    }

    public synchronized void registerRemoteDataCallbacks(IRemoteDataCallback dataCallback) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                if (dataCallback != null) {
                    mRemoteDataCallbacks.add(dataCallback);
                }
            }
        });

    }

    public synchronized void unregisterRemoteDataCallbacks(IRemoteDataCallback dataCallback) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                if (dataCallback != null) {
                    mRemoteDataCallbacks.add(dataCallback);
                }
            }
        });

    }

    public boolean isServiceConnect() {
        return mServiceConnect;
    }

    public void destroy() {
        mConnectListeners.clear();
        mRemoteProxy.disconnectRemoteService();
    }
}
