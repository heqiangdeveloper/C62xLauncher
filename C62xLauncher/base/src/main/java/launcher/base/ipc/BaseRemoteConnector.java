package launcher.base.ipc;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import launcher.base.async.AsyncSchedule;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.PollingTask;

public class BaseRemoteConnector {

    private volatile boolean startBindService = false;
    private volatile boolean mServiceConnect;
    private Set<IConnectListener> mConnectListeners = new HashSet<>();
    private Set<IRemoteDataCallback> mRemoteDataCallbacks = new HashSet<>();
    private RemoteProxy mRemoteProxy;
    protected String TAG;

    public BaseRemoteConnector(@NonNull RemoteProxy remoteProxy) {
        mRemoteProxy = remoteProxy;
        mRemoteProxy.setConnectListener(createConnectListener());
        mRemoteProxy.setRemoteDataCallback(createRemoteDataCallback());
        TAG = this.getClass().getSimpleName();
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

            @Override
            public void onServiceDied() {
                mServiceConnect = false;
                notifyConnectDied();
            }
        };
    }


    private IRemoteDataCallback createRemoteDataCallback() {
        return new IRemoteDataCallback() {
            @Override
            public void notifyData(Object o) {
                AsyncSchedule.execute(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataCallback(o);
                    }
                });
            }
//            @Override
//            public <T> void notifyData(T t) {
//                AsyncSchedule.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyDataCallback(t);
//                    }
//                });
//            }
        };
    }

    private <T> void notifyDataCallback(T t) {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                EasyLog.i(TAG, "notifyDataCallback , listeners:" + mRemoteDataCallbacks);
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

    private void notifyConnectDied() {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                for (IConnectListener connectListener : mConnectListeners) {
                    connectListener.onServiceDied();
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
        PollingTask pollingTask = new PollingTask(0, 2000, TAG) {
            @Override
            protected void executeTask() {
                mRemoteProxy.connectRemoteService(context);
            }

            @Override
            protected boolean enableExit() {
                return mServiceConnect;
            }
        };
        pollingTask.execute();
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
                    mRemoteDataCallbacks.remove(dataCallback);
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
