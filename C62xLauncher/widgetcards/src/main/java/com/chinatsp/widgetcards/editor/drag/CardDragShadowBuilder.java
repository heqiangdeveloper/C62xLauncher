package com.chinatsp.widgetcards.editor.drag;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 *只是为了获取drag的触发事件 不绘制拖动的view
 */
public class CardDragShadowBuilder extends View.DragShadowBuilder {
    private final WeakReference<View> mView;
    public CardDragShadowBuilder(){
        mView = new WeakReference<>(null);
    }
    public CardDragShadowBuilder(View view){
        mView = new WeakReference<>(view);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        //nothing
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        final View view = mView.get();
        if (view != null) {
            shadowSize.set(view.getWidth(), view.getHeight());
            shadowTouchPoint.set(shadowSize.x/2, shadowSize.y/2);
        }
    }

    public void showShadow(){

    }
}
