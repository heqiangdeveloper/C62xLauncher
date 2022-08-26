package launcher.base.ipc;

import android.content.Context;

public interface RemoteProxy {
    void setConnectListener(IConnectListener connectListener);

    void setRemoteDataCallback(IRemoteDataCallback remoteCallback);

    void connectRemoteService(Context context);

    void disconnectRemoteService();

    void requestData(IOnRequestListener onRequestListener);
}
