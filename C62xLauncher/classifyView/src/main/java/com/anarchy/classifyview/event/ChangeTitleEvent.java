package com.anarchy.classifyview.event;

public class ChangeTitleEvent extends Event {
    private int parentIndex;
    private String title;

    public ChangeTitleEvent(int parentIndex, String title) {
        this.parentIndex = parentIndex;
        this.title = title;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
