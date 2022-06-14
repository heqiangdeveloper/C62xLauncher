package com.chinatsp.widgetcards.editor.drag;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class DragViewWrapper {
    private View mView;
    private RecyclerView mRecyclerView;

    public DragViewWrapper(View view, RecyclerView recyclerView) {
        mView = view;
        mRecyclerView = recyclerView;
    }

    public View getView() {
        return mView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public int getPositionInList() {
        return mRecyclerView.getChildAdapterPosition(mView);
    }
}
