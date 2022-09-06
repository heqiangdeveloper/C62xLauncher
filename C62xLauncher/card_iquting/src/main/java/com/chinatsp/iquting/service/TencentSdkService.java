package com.chinatsp.iquting.service;

import com.tencent.wecarflow.controlsdk.FlowPlayControl;

import launcher.base.service.tencentsdk.ITencentSdkService;

public class TencentSdkService implements ITencentSdkService {
    public TencentSdkService() {
    }

    @Override
    public void closeUI() {
        FlowPlayControl.getInstance().closeUI();
    }
}
