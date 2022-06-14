package com.chinatsp.widgetcards.editor.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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
    private int mPositionInList;

    private RecyclerView mRecyclerView;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    public int getPositionInList() {
        return mPositionInList;
    }

    public void setPositionInList(int positionInList) {
        this.mPositionInList = positionInList;
    }
}
