package com.anarchy.classifyview.event;

public class ChangeSubTitleEvent extends Event {
    private String title;

    public ChangeSubTitleEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
