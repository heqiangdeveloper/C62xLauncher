package com.common.xui.widget.banner.transform;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * 向上旋转切换
 */
public class RotateUpTransformer implements ViewPager.PageTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        final float width = page.getWidth();
        final float rotation = ROT_MOD * position;

        page.setPivotX(width * 0.5f);
        page.setPivotY(0f);
        page.setTranslationX(0f);
        page.setRotation(rotation);
    }
}
