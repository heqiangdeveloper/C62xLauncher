package com.chinatsp.weaher.repository;

import launcher.base.ipc.IConnectListener;

public interface IConnectWeatherListener extends IConnectListener {
    <T> void onSuccess(T t);
}
