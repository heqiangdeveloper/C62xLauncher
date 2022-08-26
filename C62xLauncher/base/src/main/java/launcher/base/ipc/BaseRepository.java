package launcher.base.ipc;

import android.content.Context;

import androidx.annotation.NonNull;

public abstract class BaseRepository {
    private Context mContext;
    protected BaseRemoteConnector mRemoteConnector;
    private volatile boolean mInitialing = false;
    public void init(@NonNull Context context) {
        if (mInitialing) {
            return;
        }
        mInitialing = true;
        this.mContext = context.getApplicationContext();
        if (mRemoteConnector == null) {
            mRemoteConnector = createRemoteConnector(mContext);
        }
        if (mRemoteConnector != null) {
            mRemoteConnector.bindServiceAsync(mContext);
        }
    }

    protected abstract BaseRemoteConnector createRemoteConnector(Context context);

    public void registerDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.registerRemoteDataCallbacks(remoteDataCallback);
        }
    }
    public void unregisterDataCallback(IRemoteDataCallback remoteDataCallback) {
        if (mRemoteConnector != null) {
            mRemoteConnector.registerRemoteDataCallbacks(remoteDataCallback);
        }
    }

    protected void destroy(){
        mContext = null;
        if (mRemoteConnector != null) {
            mRemoteConnector.destroy();
        }
    };
}
