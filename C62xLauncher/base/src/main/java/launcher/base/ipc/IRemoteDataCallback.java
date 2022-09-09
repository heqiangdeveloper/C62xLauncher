package launcher.base.ipc;

public interface IRemoteDataCallback<T> {
     void notifyData(T t);
}
