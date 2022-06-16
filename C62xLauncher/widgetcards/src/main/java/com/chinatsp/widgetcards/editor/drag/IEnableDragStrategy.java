package com.chinatsp.widgetcards.editor.drag;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseRcvAdapter;

public interface IEnableDragStrategy  {
    /**
     * @param adapter 元素所在RecyclerView的adapter
     * @param position 元素在RecyclerView中的位置
     * @return 元素是否允许被拖动
     */
    boolean enableDrag(BaseRcvAdapter<LauncherCard> adapter, int position);

    /**
     * @param targetViewWrapper 被交换的目标ViewWrapper
     * @param dragViewRcv 正在拖拽的元素所在RecyclerView
     * @param dragView 正在被拖拽的元素
     * @return 元素是否允许被交换
     */
    boolean enableSwipe(DragViewWrapper targetViewWrapper, RecyclerView dragViewRcv, View dragView);
}
