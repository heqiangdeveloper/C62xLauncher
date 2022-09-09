package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.Cmd;
import com.chinatsp.vehicle.controller.ICmdCallback;
import com.chinatsp.vehicle.controller.IDataResolver;

interface IOuterController {

    void doBindDataResolver(in IDataResolver resolver);

    boolean isEngineStatus(in String packageName);

    void doOuterControlCommand(in Cmd cmd, in ICmdCallback callback);
}