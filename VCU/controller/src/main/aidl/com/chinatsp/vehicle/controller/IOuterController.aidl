package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.Cmd;
import com.chinatsp.vehicle.controller.ICmdCallback;

interface IOuterController {

    boolean isEngineStatus(in String packageName);

    void doOuterControlCommand(in Cmd cmd, in ICmdCallback callback);
}