package launcher.base.ipc;

public interface IOnRequestListener {
    <T> void onSuccess(T t);

    void onFail(String msg);
}
