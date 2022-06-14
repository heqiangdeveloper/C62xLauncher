package com.chinatsp.widgetcards.editor.drag;

import androidx.recyclerview.widget.RecyclerView;

public interface IOnSwipeFinish {
    void onSwipe(int position1, RecyclerView recyclerView1, int position2, RecyclerView recyclerView2);
}
