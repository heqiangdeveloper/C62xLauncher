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
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;

    public NaviBigCardHolder(@NonNull View rootView, NaviController controller) {
        this(rootView);
        mController = controller;
        Context context = rootView.getContext();
    }

    private NaviController mController;


    public NaviBigCardHolder(View rootView) {
        super(rootView);

        ivCardNaviSearch = rootView.findViewById(R.id.ivCardNaviSearch);
        ivCardNaviHome = rootView.findViewById(R.id.ivCardNaviHome);
        ivCardNaviCompany = rootView.findViewById(R.id.ivCardNaviCompany);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivCardNaviCompany) {
                naviToCompany();
            } else if (v == ivCardNaviSearch) {
                toSearch();
            } else if (v == ivCardNaviHome) {
                naviToHome();
            }
        }
    };

    private void toSearch() {
        mController.startSearch();
    }

    private void naviToCompany() {
        mController.naviToCompany();
    }

    private void naviToHome() {
        mController.naviToHome();
    }
}
