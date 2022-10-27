package com.anarchy.classifyview.event;

public class ChangeSubTitleEvent extends Event {
    private int position;
    private String title;

    public ChangeSubTitleEvent(String title,int position) {
        this.position = position;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
