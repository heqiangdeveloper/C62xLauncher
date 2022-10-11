// ICmdCallback.aidl
package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.BaseCmd;

interface ICmdCallback {

    void onCmdHandleResult(in BaseCmd cmd);
}