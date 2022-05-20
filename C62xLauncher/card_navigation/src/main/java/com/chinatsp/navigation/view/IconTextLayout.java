package com.chinatsp.navigation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.navigation.R;

public class IconTextLayout extends ConstraintLayout {
    public IconTextLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public IconTextLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IconTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private ImageView mIvIcon;
    private TextView mTvText;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_icon_text, this);
        mIvIcon = findViewById(R.id.ivIcon);
        mTvText = findViewById(R.id.tvText);
    }

    public void setImageResource(int resId) {
        mIvIcon.setImageResource(resId);
    }
    public void setText(String Text) {
        mTvText.setText(Text);
    }
    public void setText(int id) {
        mTvText.setText(id);
    }
}
