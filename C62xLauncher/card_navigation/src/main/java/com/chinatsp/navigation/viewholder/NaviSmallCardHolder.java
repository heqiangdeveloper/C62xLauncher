package com.chinatsp.navigation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.autonavi.autoaidlwidget.AutoAidlWidgetView;
import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.NavigationUtil;
import com.chinatsp.navigation.R;

import launcher.base.utils.EasyLog;

public class NaviSmallCardHolder extends NaviCardHolder {
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviArrow;
    private TextView tvCardNaviMyLocation;
    private ImageView ivCardNaviInstruction;
    private TextView tvCardNaviInstruction;

    public NaviSmallCardHolder(@NonNull View rootView, NaviController controller) {
        this(rootView);
        mController = controller;
    }

    private NaviController mController;
    private String TAG = "NaviSmallCardHolder ";

    public NaviSmallCardHolder(View rootView) {
        super(rootView);
        ivCardNaviSearch = rootView.findViewById(R.id.ivCardNaviSearch);
        ivCardNaviHome = rootView.findViewById(R.id.ivCardNaviHome);
        ivCardNaviCompany = rootView.findViewById(R.id.ivCardNaviCompany);
        ivCardNaviArrow = rootView.findViewById(R.id.ivCardNaviArrow);
        tvCardNaviMyLocation = rootView.findViewById(R.id.tvCardNaviMyLocation);
        ivCardNaviInstruction = rootView.findViewById(R.id.ivCardNaviInstruction);
        tvCardNaviInstruction = rootView.findViewById(R.id.tvCardNaviInstruction);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {
        ivCardNaviCompany.setVisibility(View.VISIBLE);
        ivCardNaviHome.setVisibility(View.VISIBLE);
        ivCardNaviSearch.setVisibility(View.VISIBLE);

        ivCardNaviArrow.setVisibility(View.GONE);
        tvCardNaviMyLocation.setVisibility(View.GONE);

        ivCardNaviInstruction.setVisibility(View.VISIBLE);
        tvCardNaviInstruction.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshFreeMode() {
        NavigationUtil.logD(TAG + "refreshFreeMode");
        ivCardNaviCompany.setVisibility(View.VISIBLE);
        ivCardNaviHome.setVisibility(View.VISIBLE);
        ivCardNaviSearch.setVisibility(View.VISIBLE);

        ivCardNaviInstruction.setVisibility(View.GONE);
        tvCardNaviInstruction.setVisibility(View.GONE);

        ivCardNaviArrow.setVisibility(View.VISIBLE);
        tvCardNaviMyLocation.setVisibility(View.VISIBLE);

    }

    @Override
    public void setLocation(String myLocationName) {
        tvCardNaviMyLocation.setText(myLocationName);
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
