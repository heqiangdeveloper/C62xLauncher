package com.chinatsp.navigation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.NavigationUtil;
import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.DriveDirection;
import com.chinatsp.navigation.repository.RoundIslandUtil;
import com.chinatsp.navigation.viewholder.lane.LaneListAdapter;

import java.util.List;
import java.util.Locale;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.EasyLog;

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
    private ImageView ivCardNaviTBTDirectIcon;
    private TextView tvCardNaviTBTDirectDistance;
    private TextView tvCardNaviTBTDirectDistanceUnit;
    private TextView tvCardNaviTBTRemainDistance;
    private TextView tvCardNaviTBTRemainTime;
    private TextView tvCardNaviTBTArriveTime;
    private RecyclerView rcvLaneInfo;
    private LaneListAdapter mLaneListAdapter;
    private boolean enableTotalLaneShow = false;

    public NaviBigCardHolder(@NonNull View rootView, NaviController controller) {
        this(rootView);
        mController = controller;
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
        ivCardNaviTBTDirectIcon = rootView.findViewById(R.id.ivCardNaviTBTDirectIcon);
        tvCardNaviTBTDirectDistance = rootView.findViewById(R.id.tvCardNaviTBTDirectDistance);
        tvCardNaviTBTDirectDistanceUnit = rootView.findViewById(R.id.tvCardNaviTBTDirectDistanceUnit);

        tvCardNaviTBTRemainDistance = rootView.findViewById(R.id.tvCardNaviTBTRemainDistance);
        tvCardNaviTBTRemainTime = rootView.findViewById(R.id.tvCardNaviTBTRemainTime);
        tvCardNaviTBTArriveTime = rootView.findViewById(R.id.tvCardNaviTBTArriveTime);

        rcvLaneInfo = rootView.findViewById(R.id.rcvLaneInfo);
        initLaneRcv();

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
    }

    private void initLaneRcv() {
        mLaneListAdapter = new LaneListAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvLaneInfo.setLayoutManager(layoutManager);
        rcvLaneInfo.setAdapter(mLaneListAdapter);
        if (!enableTotalLaneShow) {
            SimpleRcvDecoration spaceDivider = new SimpleRcvDecoration(60, layoutManager);
            Drawable divideDrawable = AppCompatResources.getDrawable(mContext, R.drawable.tbt_lane_divide_line);
            if (divideDrawable != null) {
                spaceDivider.setDrawable(divideDrawable);
            }
            rcvLaneInfo.addItemDecoration(spaceDivider);
        }
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
    public void refreshNaviGuideInfo(GuideInfo guideInfo, DriveDirection driveDirection) {
        if (guideInfo == null) {
            return;
        }
        updateOverallRouteInfo(guideInfo);
        updateTBTDirect(guideInfo, driveDirection);
    }

    /**
     * 更新导航整体信息: 总里程, 总耗时, 达到时间
     */
    private void updateOverallRouteInfo(GuideInfo guideInfo) {
        tvCardNaviTBTRemainDistance.setText(NavigationUtil.getReadableDistanceKM(guideInfo.getRouteRemainDis()));
        tvCardNaviTBTRemainTime.setText(NavigationUtil.getReadableRemainTime(guideInfo.getRouteRemainTime()));
        tvCardNaviTBTArriveTime.setText(guideInfo.getEtaText());
    }

    /**
     * 更新当前道路/转向/达到点信息
     */
    private void updateTBTDirect(GuideInfo guideInfo, DriveDirection driveDirection) {
        EasyLog.i("refreshTBTDirect ", "driveDirection " + driveDirection);
        EasyLog.i("refreshTBTDirect ", guideInfo.getNextRoadName() + "  " + guideInfo.getSegRemainDis() + "米");

        tvCardNaviTurnRoadName.setText(guideInfo.getNextRoadName());
        updateTBTDirectDistance(guideInfo.getSegRemainDis());
        if (RoundIslandUtil.isRoundIsland(guideInfo.getIcon())) {
            int islandIcon = RoundIslandUtil.getIsland(guideInfo.getRoundAboutNum(), RoundIslandUtil.isRoundByClockWise(guideInfo.getIcon()));
            ivCardNaviTBTDirectIcon.setImageResource(islandIcon);
        } else {
            if (driveDirection != null) {
                ivCardNaviTBTDirectIcon.setImageResource(driveDirection.getIconRes());
            }
        }
    }

    /**
     * 显示规则: 1000米内, 使用单位米. 超过则用千米.
     * 1-10km内, 精确到小数点后1位. 例如: 9.2公里.
     * 超过10km, 精确到个位数. 例如: 22公里.
     *
     * @param segRemainDis
     */
    private void updateTBTDirectDistance(int segRemainDis) {
        int distance = Math.max(segRemainDis, 0);
        if (distance < 1000) {
            tvCardNaviTBTDirectDistance.setText(String.valueOf(distance));
            tvCardNaviTBTDirectDistanceUnit.setText("米");
        } else {
            tvCardNaviTBTDirectDistanceUnit.setText("公里");
            float km = (float) distance / 1000f;
            if (distance > 10000) {
                // 10 公里
                int kmInt = (int) km;
                tvCardNaviTBTDirectDistance.setText(String.valueOf(kmInt));
            } else {
                // 9.9公里
                String kmStr = String.format(Locale.getDefault(), "%.1f", km);
                tvCardNaviTBTDirectDistance.setText(kmStr);
            }
        }
    }

    public void refreshNaviLaneInfo(TrafficLaneModel trafficLaneModel) {
        List<TrafficLaneModel.LaneInfo> laneInfoList = trafficLaneModel.getTrafficLaneInfos();
        if (laneInfoList == null || laneInfoList.isEmpty()) {
            return;
        }
        int n = laneInfoList.size();
        EasyLog.i("refreshNaviLaneInfo ", "BEGIN , lane size:"+n);

        for (TrafficLaneModel.LaneInfo laneInfo : laneInfoList) {
            EasyLog.d("refreshNaviLaneInfo ", laneInfo.getTrafficLaneNo() + " , " + laneInfo.getTrafficLaneExtended() + " , " + laneInfo.getTrafficLaneIcon());
            int id = laneInfo.getTrafficLaneNo();
        }
        filter(laneInfoList);
        mLaneListAdapter.setData(laneInfoList);
    }

    private void filter(List<TrafficLaneModel.LaneInfo> laneInfoList) {
        if (laneInfoList == null || laneInfoList.isEmpty()) {
            return;
        }
        if (enableTotalLaneShow) {
            return;
        }
        // 最多只能有4个
        int max = 4, size = laneInfoList.size();
        if (size > max) {
            List<TrafficLaneModel.LaneInfo> more4Lanes = laneInfoList.subList(max, laneInfoList.size());
            EasyLog.d("XXXTTTT", "filter " + laneInfoList.size() + ", " + more4Lanes.size());
            for (int i = size - 1; i >= max; i--) {
                laneInfoList.remove(i);
            }
            EasyLog.d("XXXTTTT", "filter " + laneInfoList.size());
        }
    }
}
