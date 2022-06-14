package com.chinatsp.widgetcards.editor.drag;

import android.view.View;
import android.widget.ImageView;

import com.chinatsp.widgetcards.R;

public class DragItemViewHelp implements IDragItemView {

    private View mView;
    private ImageView mBgView;
    private ImageView mEmptyBgView;

    public DragItemViewHelp(View view) {
        mView = view;
        mBgView = view.findViewById(R.id.ivCardSelectedBg);
        mEmptyBgView = view.findViewById(R.id.ivCardSelectedEmptyBg);
    }

    @Override
    public void becomeEmpty() {
        mBgView.setVisibility(View.GONE);
        mEmptyBgView.setVisibility(View.VISIBLE);
    }

    @Override
    public void restore() {
        mBgView.setVisibility(View.VISIBLE);
        mEmptyBgView.setVisibility(View.GONE);
    }
}
