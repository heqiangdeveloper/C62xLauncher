package com.chinatsp.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.content.res.AppCompatResources;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.gaode.bean.Address;
import com.chinatsp.navigation.gaode.bean.GaoDeResponse;
import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.MapStatus;
import com.chinatsp.navigation.gaode.bean.NavigationStatus;
import com.chinatsp.navigation.gaode.bean.RoadInfo;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.DriveDirection;
import com.chinatsp.navigation.repository.INaviCallback;
import com.chinatsp.navigation.repository.NaviRepository;
import com.chinatsp.navigation.repository.ResponseParser;


import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkUtils;
import launcher.base.utils.recent.RecentAppHelper;

public class NaviController implements INaviCallback {
    private NaviCardView mView;
    private String TAG = "NaviController ";
    public static final int STATE_CRUISE = NavigationStatus.STATUS_CRUISE;
    public static final int STATE_IN_NAVIGATION = NavigationStatus.STATUS_IN_NAVIGATION;
    public static final int STATE_IN_NAVIGATION_MOCK = NavigationStatus.STATUS_IN_NAVIGATION_MOCK; // 模拟导航
    private int mState = STATE_CRUISE;
    private NaviRepository mNaviRepository = NaviRepository.getInstance();
    private Handler mHandler = new android.os.Handler(Looper.getMainLooper());
    private GaoDeResponse<GuideInfo> tempGuideInfoGaoDeResponse;

    public NaviController(NaviCardView view) {
        mView = view;
        Context context = view.getContext();
        mNaviRepository.init(context);

        initAidlWidgetManager(context);
        mNaviRepository.registerDataCallback(mIRemoteDataCallback);
        mNaviRepository.registerConnectListener(mConnectListener);
//        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);

    }

    private void checkNetwork() {
        if (!NetworkUtils.isNetworkAvailable(mView.getContext())) {
            mView.showNetWorkError();
        } else {
            mView.hideNetWorkError();
        }
    }

    private void initAidlWidgetManager(Context context) {
        AutoAidlWidgetManager.getInstance().init(context.getApplicationContext());
        AutoAidlWidgetManager.getInstance().setNeedDisTouchEvent(true);
//        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, R.drawable.card_bg_large);
//        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, R.drawable.testgaode);
//        AutoAidlWidgetManager.getInstance().setShadeBitmap(bitmap);
    }

    public void refreshInitView() {
        mState = STATE_CRUISE;
        mView.refreshState(mState);
//        checkNetwork();
    }

    IRemoteDataCallback<String> mIRemoteDataCallback = new IRemoteDataCallback<String>() {
        @Override
        public void notifyData(String s) {
            new ResponseParser(s, NaviController.this)
                    .parse();
        }
    };
    IConnectListener mConnectListener = new IConnectListener() {
        @Override
        public void onServiceConnected() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getNavigationStatus();
                }
            });
        }

        @Override
        public void onServiceDisconnected() {

        }

        @Override
        public void onServiceDied() {

        }
    };

    private NetworkObserver mNetworkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
//            if (isConnected) {
//                hideNetWorkError();
//            } else {
//                mView.showNetWorkError();
//            }
        }
    };

    private void hideNetWorkError() {
        mView.hideNetWorkError();
        mView.refreshState(mState);
    }

    public void goMyLocation() {
        NavigationUtil.logD(TAG + "goMyLocation");
        mNaviRepository.getLocation();
    }

    public void getNavigationStatus() {
        NavigationUtil.logD(TAG + "getNavigationStatus");
        mNaviRepository.getNavigationStatus();
    }

    @Override
    public void receiveMyLocation(GaoDeResponse<Address> gaoDeResponse) {
        Address address = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveMyLocation address:" + address);
    }

    @Override
    public void receiveNavigationStatus(GaoDeResponse<NavigationStatus> gaoDeResponse) {
        NavigationStatus status = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveNavigationStatus status:" + status);
        if (status != null) {
            mState = status.getStatus();
        }
    }

    @Override
    public void receiveCurRoadInfo(GaoDeResponse<RoadInfo> gaoDeResponse) {
        RoadInfo roadInfo = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveCurRoadInfo roadInfo:" + roadInfo);
        String roadName;
        boolean unknownLocation = true;
        if (roadInfo != null) {
            roadName = roadInfo.getCurRoadName();
            if (TextUtils.isEmpty(roadName)) {
                roadName = mView.getContext().getString(R.string.card_navi_msg_road_name);
            } else {
                unknownLocation = false;
            }
        } else {
            roadName = mView.getContext().getString(R.string.card_navi_msg_road_name);
        }
        mView.refreshMyLocation(roadName, unknownLocation);
    }

    @Override
    public void receiveNaviGuideInfo(GaoDeResponse<GuideInfo> gaoDeResponse) {
        GuideInfo guideInfo = gaoDeResponse.getData();
        tempGuideInfoGaoDeResponse = gaoDeResponse;
        NavigationUtil.logD(TAG + "receiveNaviGuideInfo");
        if (guideInfo != null) {
            // GuideInfo包含导航状态, 0: 导航, 1: 模拟导航, 2: 巡航
            if (guideInfo.getType() == 0 && mState != STATE_IN_NAVIGATION) {
                // 导航
                mState = STATE_IN_NAVIGATION;
                mView.refreshState(mState);
            } else if (guideInfo.getType() == 1 && mState != STATE_IN_NAVIGATION_MOCK) {
                // 模拟导航
                mState = STATE_IN_NAVIGATION_MOCK;
                mView.refreshState(mState);
            }
            mView.refreshGuideInfo(guideInfo, DriveDirection.parseFromType(guideInfo.getIcon()));
        }
    }

    @Override
    public void receiveMapStatus(GaoDeResponse<MapStatus> gaoDeResponse) {
        MapStatus mapStatus = gaoDeResponse.getData();
        if (mapStatus == null) {
            return;
        }
        int autoStatus = mapStatus.getAutoStatus();
        NavigationUtil.logD(TAG + "receiveMapStatus autoStatus:" + autoStatus);
        boolean needRefreshState = true;
        if (autoStatus == MapStatus.START_NAVIGATION) {
            mState = STATE_IN_NAVIGATION;
        } else if (autoStatus == MapStatus.START_MOCK_NAVIGATION) {
            mState = STATE_IN_NAVIGATION_MOCK;
        } else if (autoStatus == MapStatus.STOP_NAVIGATION) {
            mState = STATE_CRUISE;
        } else if (autoStatus == MapStatus.STOP_MOCK_NAVIGATION) {
            mState = STATE_CRUISE;
        } else {
            needRefreshState = false;
        }
        if (needRefreshState) {
            mView.refreshState(mState);
        }
    }

    @Override
    public void receiveTrafficLane(GaoDeResponse<TrafficLaneModel> gaoDeResponse) {
        NavigationUtil.logD(TAG + "receiveTrafficLane");
        TrafficLaneModel trafficLaneModel = gaoDeResponse.getData();
        if (trafficLaneModel == null) {
            return;
        }
        mView.refreshLaneInfo(trafficLaneModel);
    }

    public void startSearch() {
        NavigationUtil.logD(TAG + "startSearch");
        mNaviRepository.startSearchPage();
    }

    public void naviToCompany() {
        NavigationUtil.logD(TAG + "naviToCompany");
        mNaviRepository.startNaviToCompanyPage();
    }

    public void naviToHome() {
        NavigationUtil.logD(TAG + "naviToHome");
        mNaviRepository.startNaviToHomePage();
    }

    public void refreshPageState() {
        mView.refreshState(mState);
        if (tempGuideInfoGaoDeResponse != null) {
            GuideInfo guideInfo = tempGuideInfoGaoDeResponse.getData();
            if (guideInfo != null) {
                mView.refreshGuideInfo(tempGuideInfoGaoDeResponse.getData(), DriveDirection.parseFromType(guideInfo.getIcon()));
            }
        }
//        checkNetwork();
    }

    /**
     * 10.23版本后不可用了
     */
    @Deprecated
    public void toMainMap() {
        NavigationUtil.logD(TAG + "toMainMap");
        mNaviRepository.startMainMapPage();
    }

    public void exitNaviStatus() {
        NavigationUtil.logD(TAG + "exitNaviStats");
        mNaviRepository.exitNaiveStatus();
    }

    void toApp(Context context) {
        RecentAppHelper.launchApp(context, "com.autonavi.amapauto");
    }
}
