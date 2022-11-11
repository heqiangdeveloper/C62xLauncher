package com.chinatsp.weaher.viewholder.indicator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chinatsp.weaher.R;
import com.chinatsp.weaher.WeatherUtil;

import launcher.base.utils.EasyLog;

public class PointIndicator implements ISelectIndicator {

    private int mCurrentIndex;
    private View[] mViews;
    private ViewGroup mContainer;

    public PointIndicator(ViewGroup container) {
        mContainer = container;
    }

    @Override
    public void reset(int max) {
        WeatherUtil.logD("PointIndicator reset max:"+max);
        mViews = new View[max];
        mContainer.removeAllViews();
        for (int i = 0; i < max; i++) {
            mViews[i] = createIndexView(mContainer.getContext(), R.drawable.card_weather_indicator_selector);
            mContainer.addView(mViews[i]);
        }
        resetUIState();
        select(0);
    }

    @Override
    public View createIndexView(Context context, int drawableRes) {
        View view = new View((context));
        // 尺寸为18*18
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(18,18);
        view.setBackgroundResource(drawableRes);
        view.setLayoutParams(layoutParams);
        return view;
    }


    @Override
    public void select(int position) {
        WeatherUtil.logD("PointIndicator select position:"+position);
        if (mViews == null) {
            return;
        }
        if (position < 0 || position >= mViews.length) {
            return;
        }
        mCurrentIndex = position;
        for (View view : mViews) {
            if (view != null) {
                view.setSelected(false);
            }
        }
        View selectView = mViews[position];
        if (selectView != null) {
            selectView.setSelected(true);
        }
    }

    @Override
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    @Override
    public int getMax() {
        if (mViews == null) {
            return 0;
        }
        return mViews.length;
    }


    private void resetUIState() {
        if (mViews == null) {
            return;
        }
        for (View view : mViews) {
            if (view != null) {
                view.setSelected(false);
            }
        }
    }

}
