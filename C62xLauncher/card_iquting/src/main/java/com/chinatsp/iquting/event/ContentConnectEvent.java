package com.chinatsp.iquting.event;

public class ContentConnectEvent extends Event{
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final int CONNECTIONDIED = 3;
    private int type;

    public ContentConnectEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
