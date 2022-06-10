package com.chinatsp.widgetcards.editor.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DragSwipeView extends View {
    public DragSwipeView(Context context) {
        super(context);
    }

    public DragSwipeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragSwipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DragSwipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    private int positionInList;

    public int getPositionInList() {
        return positionInList;
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }
}
