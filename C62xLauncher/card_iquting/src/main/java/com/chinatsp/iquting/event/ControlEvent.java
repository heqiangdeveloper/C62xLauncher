package com.chinatsp.iquting.event;

public class ControlEvent extends Event{
    private int position;
    private String songId;
    private int type;

    public ControlEvent(int type, int position, String songId) {
        this.position = position;
        this.songId = songId;
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
