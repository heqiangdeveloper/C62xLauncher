package com.chinatsp.widgetcards.editor.drag;

import android.view.View;

public interface IDragCardListener {
    void onStartDrag(View view);

    void onDrag(float dx, float dy);

    void onCancel();

    void onSwipe();
}
