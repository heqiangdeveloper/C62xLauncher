package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.AirCmd;
import com.chinatsp.vehicle.controller.bean.CarCmd;
import com.chinatsp.vehicle.controller.ICmdCallback;
import com.chinatsp.vehicle.controller.IDataResolver;

interface IOuterController {

    boolean isEngineStatus(in String packageName);

    void doBindDataResolver(in IDataResolver resolver);

    void doAirControlCommand(in AirCmd cmd, in ICmdCallback callback);

    void doCarControlCommand(in CarCmd cmd, in ICmdCallback callback);
}