package com.chinatsp.navigation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.NavigationUtil;
import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.GuideInfo;

public class NaviSmallCardHolder extends NaviCardHolder {
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviArrow;
    private TextView tvCardNaviMyLocation;
    private ImageView ivCardNaviInstruction;
    private TextView tvCardNaviInstruction;
    private TextView tvCardNaviRoadTip;
    private ImageView ivCardNetworkErr;
    private TextView tvCardNetworkErr;
    private View layoutCardNaviInstruction;

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

        layoutCardNaviInstruction = rootView.findViewById(R.id.layoutCardNaviInstruction);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);


        rootView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {
        ivCardNaviArrow.setVisibility(View.GONE);
        tvCardNaviMyLocation.setVisibility(View.GONE);

        layoutCardNaviInstruction.setVisibility(View.VISIBLE);

    }

    @Override
    public void refreshFreeMode() {
        NavigationUtil.logD(TAG + "refreshFreeMode");

        layoutCardNaviInstruction.setVisibility(View.GONE);


        ivCardNaviArrow.setVisibility(View.VISIBLE);
        tvCardNaviMyLocation.setVisibility(View.VISIBLE);

    }

    @Override
    public void setLocation(String myLocationName) {
        tvCardNaviMyLocation.setText(myLocationName);
    }

    @Override
    public void showNetworkError() {
        ivCardNaviArrow.setVisibility(View.GONE);
        tvCardNaviMyLocation.setVisibility(View.GONE);
        ivCardNetworkErr.setVisibility(View.VISIBLE);
        tvCardNetworkErr.setVisibility(View.VISIBLE);
        layoutCardNaviInstruction.setVisibility(View.GONE);
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
            } else {
                toApp();
            }
        }
    };

    private void toApp() {
        mController.toMainMap();
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

    public void refreshNaviGuideInfo(GuideInfo guideInfo) {
        if (guideInfo == null) {
            return;
        }
        ivCardNaviInstruction.setImageResource(R.drawable.card_navi_tbt_direct_right);
        tvCardNaviInstruction.setText("前方直行");
        tvCardNaviRoadTip.setText("进入"+guideInfo.getNextRoadName());
    }
}
