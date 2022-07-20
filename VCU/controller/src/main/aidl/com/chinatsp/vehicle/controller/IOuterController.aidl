package com.chinatsp.vehicle.controller;

import com.chinatsp.vehicle.controller.bean.Cmd;
import com.chinatsp.vehicle.controller.ICmdCallback;

interface IOuterController {

    void doOuterControlCommand(in Cmd cmd, in ICmdCallback callback);
}