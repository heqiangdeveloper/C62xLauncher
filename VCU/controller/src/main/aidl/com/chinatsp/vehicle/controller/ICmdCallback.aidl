// ICmdCallback.aidl
package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.Cmd;

interface ICmdCallback {

    void onCmdHandleResult(in Cmd cmd);
}