package com.chinatsp.navigation.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.R;

public class NaviBigCardHolder extends NaviCardHolder{
    public NaviBigCardHolder(@NonNull View rootView, NaviController controller) {
        this(rootView);
        mController = controller;
        Context context = rootView.getContext();
    }

    private NaviController mController;


    public NaviBigCardHolder(View rootView) {
        super(rootView);
    }

    @Override
    public void refreshNavigation() {

    }

    @Override
    public void refreshFreeMode() {

    }
    @Override
    public void setLocation(String myLocationName) {
        
    }
}
