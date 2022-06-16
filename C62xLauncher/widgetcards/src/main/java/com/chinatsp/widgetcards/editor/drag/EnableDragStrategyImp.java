package com.chinatsp.widgetcards.editor.drag;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.manager.CardManager;

import java.util.List;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;

public class EnableDragStrategyImp implements IEnableDragStrategy {
    private static final String TAG = "EnableDragStrategyImp";

    @Override
    public boolean enableDrag(BaseRcvAdapter<LauncherCard> adapter, int position) {
        List<LauncherCard> data = adapter.getData();
        if (data == null) {
            return false;
        }
        boolean indexOutOfArray = IndexCheck.indexOutOfArray(data, position);
        if (!indexOutOfArray) {
            return data.get(position).getType() != CardManager.CardType.EMPTY;
        }
        return false;
    }

    @Override
    public boolean enableSwipe(DragViewWrapper targetViewWrapper, RecyclerView dragViewRcv, View dragView) {
        if (targetViewWrapper == null) {
            return false;
        }
        if (dragView == targetViewWrapper.getView()) {
            // 仍在选中的卡片View范围内, 所以无需交换卡片
            EasyLog.w(TAG, "findTargetView, it is self...");
            return false;
        }
        return checkSwipeNotEmptyCard(targetViewWrapper.getRecyclerView(), targetViewWrapper.getPositionInList());
    }

    public boolean checkSwipeNotEmptyCard(RecyclerView recyclerView, int position) {
        BaseRcvAdapter<LauncherCard> adapter = (BaseRcvAdapter<LauncherCard>) recyclerView.getAdapter();
        List<LauncherCard> data = adapter.getData();
        if (data == null) {
            return false;
        }
        boolean indexOutOfArray = IndexCheck.indexOutOfArray(data, position);
        if (!indexOutOfArray) {
            boolean result = data.get(position).getType() != CardManager.CardType.EMPTY;
            if (!result) {
                EasyLog.w(TAG, "showTargetHighlight, cannot swipe items which type is empty.");
            }
            return result;
        }
        return false;
    }
}
