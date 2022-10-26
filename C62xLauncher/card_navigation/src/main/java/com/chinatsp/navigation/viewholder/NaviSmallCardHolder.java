package com.chinatsp.navigation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.NavigationUtil;
import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.DriveDirection;

import launcher.base.utils.recent.RecentAppHelper;

public class NaviSmallCardHolder extends NaviCardHolder {
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviArrow;
    private TextView tvCardNaviMyLocation;
    private ImageView ivCardNaviInstruction;
    private TextView tvCardNaviInstruction;
    private TextView tvCardNaviRoadTip;
    private ImageView ivCardNaviExit;

    private ImageView ivCardNetworkErr;
    private TextView tvCardNetworkErr;
    private View layoutCardNaviTBTStatus;
    private View layoutCardNaviCruiseStatus;

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
        tvCardNaviRoadTip = rootView.findViewById(R.id.tvCardNaviRoadTip);
        ivCardNetworkErr = rootView.findViewById(R.id.ivCardNetworkErr);
        tvCardNetworkErr = rootView.findViewById(R.id.tvCardNetworkErr);
        ivCardNaviExit = rootView.findViewById(R.id.ivCardNaviExit);

        layoutCardNaviTBTStatus = rootView.findViewById(R.id.layoutCardNaviTBTStatus);
        layoutCardNaviCruiseStatus = rootView.findViewById(R.id.layoutCardNaviCruiseStatus);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
        ivCardNaviExit.setOnClickListener(mOnClickListener);


        rootView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {
        layoutCardNaviCruiseStatus.setVisibility(View.INVISIBLE);
        layoutCardNaviTBTStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshFreeMode() {
        NavigationUtil.logD(TAG + "refreshFreeMode");
        layoutCardNaviTBTStatus.setVisibility(View.INVISIBLE);
        layoutCardNaviCruiseStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLocation(String myLocationName) {
        tvCardNaviMyLocation.setText(myLocationName);
    }

    @Override
    public void showNetworkError() {
        ivCardNetworkErr.setVisibility(View.VISIBLE);
        tvCardNetworkErr.setVisibility(View.VISIBLE);
        layoutCardNaviCruiseStatus.setVisibility(View.GONE);
        layoutCardNaviTBTStatus.setVisibility(View.GONE);
    }

    @Override
    public void hideNetworkError() {
        ivCardNetworkErr.setVisibility(View.GONE);
        tvCardNetworkErr.setVisibility(View.GONE);
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
            } else if (v == ivCardNaviExit) {

            } else {
                toApp();
            }
        }
    };

    private void toApp() {
        RecentAppHelper.launchApp(mContext, "com.autonavi.amapauto");
    }

    private void toSearch() {
        mController.startSearch();
    }

    private void naviToCompany() {
        mController.naviToCompany();
    }

    private void naviToHome() {
        mController.naviToHome();
    }

    public void refreshNaviGuideInfo(GuideInfo guideInfo, DriveDirection driveDirection) {
        if (guideInfo == null) {
            return;
        }

        tvCardNaviRoadTip.setText("进入"+guideInfo.getNextRoadName());
        if (driveDirection != null) {
            ivCardNaviInstruction.setImageResource(driveDirection.getIconRes());
            tvCardNaviInstruction.setText(driveDirection.getNameRes());
        }
    }

    public void refreshNaviLaneInfo(TrafficLaneModel trafficLaneModel) {

    }
}
