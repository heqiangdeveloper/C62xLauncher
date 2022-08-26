package launcher.base.ipc;

public interface IConnectListener {
    void onServiceConnected();

    void onServiceDisconnected();
}
