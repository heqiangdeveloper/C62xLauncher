package com.chinatsp.navigation.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.GuideInfo;

public class NaviBigCardHolder extends NaviCardHolder {
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviBigDefaultMap;
    private View layoutCardNetworkError;
    private View layoutCardNaviStatus;
    private View layoutCardNaviCruise;
    private View surfaceViewNavi;
    private TextView tvCardNaviTurnRoadName;
    private ImageView ivCardNaviTurnOrientation;
    private TextView tvCardNaviDistanceTurn;

    public NaviBigCardHolder(@NonNull View rootView, NaviController controller) {
        this(rootView);
        mController = controller;
        Context context = rootView.getContext();
    }

    private NaviController mController;


    public NaviBigCardHolder(View rootView) {
        super(rootView);

        surfaceViewNavi = rootView.findViewById(R.id.surfaceViewNavi);
        ivCardNaviBigDefaultMap = rootView.findViewById(R.id.ivCardNaviBigDefaultMap);


        ivCardNaviSearch = rootView.findViewById(R.id.ivCardNaviSearch);
        ivCardNaviHome = rootView.findViewById(R.id.ivCardNaviHome);
        ivCardNaviCompany = rootView.findViewById(R.id.ivCardNaviCompany);
        layoutCardNetworkError = rootView.findViewById(R.id.layoutCardNetworkError);
        layoutCardNaviStatus = rootView.findViewById(R.id.layoutCardNaviStatus);
        layoutCardNaviCruise = rootView.findViewById(R.id.layoutCardNaviCruise);

        tvCardNaviTurnRoadName = rootView.findViewById(R.id.tvCardNaviTurnRoadName);
        ivCardNaviTurnOrientation = rootView.findViewById(R.id.ivCardNaviTurnOrientation);
        tvCardNaviDistanceTurn = rootView.findViewById(R.id.tvCardNaviDistanceTurn);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {
        surfaceViewNavi.setVisibility(View.VISIBLE);
        layoutCardNaviStatus.setVisibility(View.VISIBLE);
        layoutCardNaviCruise.setVisibility(View.INVISIBLE);
    }

    @Override
    public void refreshFreeMode() {
        surfaceViewNavi.setVisibility(View.VISIBLE);
        layoutCardNaviStatus.setVisibility(View.INVISIBLE);
        layoutCardNaviCruise.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLocation(String myLocationName) {

    }

    @Override
    public void showNetworkError() {
        surfaceViewNavi.setVisibility(View.INVISIBLE);
        layoutCardNaviStatus.setVisibility(View.INVISIBLE);
        layoutCardNaviCruise.setVisibility(View.INVISIBLE);

        layoutCardNetworkError.setVisibility(View.VISIBLE);
        ivCardNaviBigDefaultMap.setVisibility(View.VISIBLE);
        layoutCardNaviCruise.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNetworkError() {
        layoutCardNetworkError.setVisibility(View.GONE);
        ivCardNaviBigDefaultMap.setVisibility(View.GONE);
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

    @Override
    public void refreshNaviGuideInfo(GuideInfo guideInfo) {
        tvCardNaviTurnRoadName.setText(guideInfo.getNextRoadName());
        tvCardNaviDistanceTurn.setText(String.valueOf(guideInfo.getSegRemainDis()));
        ivCardNaviTurnOrientation.setImageResource(R.drawable.card_navi_icon_turn_right);
    }
}
