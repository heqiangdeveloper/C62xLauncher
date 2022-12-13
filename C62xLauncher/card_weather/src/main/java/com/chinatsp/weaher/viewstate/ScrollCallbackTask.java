package com.chinatsp.weaher.viewstate;

import launcher.base.utils.flowcontrol.DebounceTask;

public abstract class ScrollCallbackTask extends DebounceTask {
    private int index;

    public ScrollCallbackTask() {
        // 1000ms 内,  只响应滚动一次
        super(1000);
        TAG = "ScrollCallbackTask";
    }

    @Override
    public void execute() {
        execute(index);
    }

    public void emit(int index) {
        this.index = index;
        super.emit();
    }

    public abstract void execute(int index);
}
