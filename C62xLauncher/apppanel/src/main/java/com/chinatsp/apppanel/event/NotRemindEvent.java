package com.chinatsp.apppanel.event;

import com.anarchy.classifyview.event.Event;

public class NotRemindEvent extends Event {
    private String packageName;
    private String reverse3;

    public NotRemindEvent(String packageName, String reverse3) {
        this.packageName = packageName;
        this.reverse3 = reverse3;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getReverse3() {
        return reverse3;
    }

    public void setReverse3(String reverse3) {
        this.reverse3 = reverse3;
    }
}
