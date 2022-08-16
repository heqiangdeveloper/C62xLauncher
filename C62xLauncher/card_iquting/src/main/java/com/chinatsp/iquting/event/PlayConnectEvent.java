package com.chinatsp.iquting.event;

public class PlayConnectEvent extends Event{
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final int BINDDIED = 3;
    public static final int ERROR = 4;
    private int type;

    public PlayConnectEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
