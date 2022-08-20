package launcher.base.utils.selector;

import android.view.View;


import java.util.ArrayList;
import java.util.Arrays;

import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.ClearUtil;

public class ViewStateSelector {

    private ArrayList<StatefulViewHolder> viewList = new ArrayList<>(10);
    private OnViewSelected onSelectListener;
    public static ViewStateSelector create(OnViewSelected onSelectListener, StatefulViewHolder... viewHolders) {
        ViewStateSelector selector = new ViewStateSelector();
        if (viewHolders != null) {
            selector.viewList.addAll(Arrays.asList(viewHolders));
        }
        selector.onSelectListener = onSelectListener;

        selector.initClickListener();
        return selector;
    }


    private int defaultSelectedIndex = 0;
    private StatefulViewHolder lastSelectViewHolder;


    private void initClickListener() {
        for (StatefulViewHolder statefulViewHolder : viewList) {
            if (statefulViewHolder != null && statefulViewHolder.getView() != null) {
                statefulViewHolder.getView().setOnClickListener(onClickListener);
            }
        }
    }

    private StatefulViewHolder getClickedItem(View view) {
        for (StatefulViewHolder statefulViewHolder : viewList) {
            View item = null;
            if (statefulViewHolder != null &&  (item = statefulViewHolder.getView())!= null) {
                if (item == view) {
                    return statefulViewHolder;
                }
            }
        }
        return null;
    }
    /**
     * 选中第一个View, 并且会触发监听回调
     */
    public void selectFirst() {
        if (!viewList.isEmpty()) {
            select(viewList.get(0));
        }
    }

    /**
     * 仅仅初始化第一个view的状态, 不会触发监听回调
     */
    public void initFirstViewState() {
        if (!viewList.isEmpty()) {
            StatefulViewHolder statefulViewHolder = viewList.get(0);
            if (statefulViewHolder != null) {
                statefulViewHolder.changeViewsState(true);
            }
        }
    }
    public void selectFirst(View view) {
        if (!viewList.isEmpty()) {
            for (StatefulViewHolder statefulViewHolder : viewList) {
                if (statefulViewHolder.check(view)) {
                    select(statefulViewHolder);
                    return;
                }
            }
        }
    }
    private void select(StatefulViewHolder viewHolder) {
        if (viewHolder == null) {
            return;
        }

        for (StatefulViewHolder statefulViewHolder : viewList) {
            if (statefulViewHolder != null) {
                statefulViewHolder.changeViewsState(statefulViewHolder == viewHolder);
            }
        }
        onSelectListener.onViewSelected(viewHolder);
        lastSelectViewHolder = viewHolder;
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            StatefulViewHolder clickedItem = getClickedItem(view);
            if (clickedItem != null) {
                select(clickedItem);
            }
        }
    };

    public void onDestroy() {
        ClearUtil.clear(viewList);
        onSelectListener = null;
        onClickListener = null;
    }

    /**
     * 仅仅切换选中效果,  不会触发回调方法
     * @param position
     */
    public void setCurrent(int position) {
        if (position >= 0 && position < viewList.size()) {
            StatefulViewHolder viewHolder = viewList.get(position);
            for (StatefulViewHolder statefulViewHolder : viewList) {
                if (statefulViewHolder != null) {
                    statefulViewHolder.changeViewsState(statefulViewHolder == viewHolder);
                }
            }
            lastSelectViewHolder = viewHolder;
        }else {
            EasyLog.w("ViewStateSelector", "position 索引越界:"+position);
        }
    }
}
