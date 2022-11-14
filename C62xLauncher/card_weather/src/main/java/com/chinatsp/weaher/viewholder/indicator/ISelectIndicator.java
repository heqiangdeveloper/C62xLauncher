package com.chinatsp.weaher.viewholder.indicator;

import android.content.Context;
import android.view.View;

public interface ISelectIndicator {
    void select(int position);

    int getCurrentIndex();

    int getMax();

    void reset(int max);

    View createIndexView(Context context, int drawableRes);
}
