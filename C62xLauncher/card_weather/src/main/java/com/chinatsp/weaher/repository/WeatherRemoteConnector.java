package com.chinatsp.weaher.repository;

import androidx.annotation.NonNull;

import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.RemoteProxy;

public class WeatherRemoteConnector extends BaseRemoteConnector {
    public WeatherRemoteConnector(@NonNull RemoteProxy remoteProxy) {
        super(remoteProxy);
    }

}
