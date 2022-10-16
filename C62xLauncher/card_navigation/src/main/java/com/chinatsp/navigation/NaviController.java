package com.chinatsp.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.content.res.AppCompatResources;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.gaode.bean.Address;
import com.chinatsp.navigation.gaode.bean.GaoDeResponse;
import com.chinatsp.navigation.gaode.bean.GuideInfo;
import com.chinatsp.navigation.gaode.bean.MapStatus;
import com.chinatsp.navigation.gaode.bean.NavigationStatus;
import com.chinatsp.navigation.gaode.bean.RoadInfo;
import com.chinatsp.navigation.repository.INaviCallback;
import com.chinatsp.navigation.repository.NaviRepository;
import com.chinatsp.navigation.repository.ResponseParser;


import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IRemoteDataCallback;
import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;

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
        NetworkStateReceiver.getInstance().registerObserver(mNetworkObserver);

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
        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, R.drawable.card_bg_large);
//        BitmapDrawable drawable = (BitmapDrawable) AppCompatResources.getDrawable(context, R.drawable.testgaode);
        Bitmap bitmap = drawable.getBitmap();
//        AutoAidlWidgetManager.getInstance().setShadeBitmap(bitmap);
    }

    public void refreshInitView() {
        mState = STATE_CRUISE;
        mView.refreshState(mState);
        checkNetwork();
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
            if (isConnected) {
                hideNetWorkError();
            } else {
                mView.showNetWorkError();
            }
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
        if (address != null) {
            mView.refreshMyLocation(address.getPoiName());
        }
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
        if (roadInfo != null) {
            mView.refreshMyLocation(roadInfo.getCurRoadName());
        }
    }

    @Override
    public void receiveNaviGuideInfo(GaoDeResponse<GuideInfo> gaoDeResponse) {
        GuideInfo guideInfo = gaoDeResponse.getData();
        tempGuideInfoGaoDeResponse = gaoDeResponse;
        NavigationUtil.logD(TAG + "receiveNaviGuideInfo");
        if (guideInfo != null) {
            // 根据观察,  在导航时, 这个type==0, 所以视为切换到导航模式
            if (guideInfo.getType() == 0) {
                mState = STATE_IN_NAVIGATION;
                mView.refreshState(mState);
            }
            mView.refreshGuideInfo(guideInfo);
        }
    }

    @Override
    public void receiveMapStatus(GaoDeResponse<MapStatus> gaoDeResponse) {
        MapStatus mapStatus = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveMapStatus mapStatus:" + mapStatus);
        if (mapStatus == null) {
            return;
        }
        int autoStatus = mapStatus.getAutoStatus();
        if (autoStatus == MapStatus.START_NAVIGATION) {
            mState = STATE_IN_NAVIGATION;
            mView.refreshState(mState);
        } else if (autoStatus == MapStatus.STOP_NAVIGATION){
            mState = STATE_CRUISE;
            mView.refreshState(mState);
        }
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
            mView.refreshGuideInfo(tempGuideInfoGaoDeResponse.getData());
        }
        checkNetwork();
    }

    public void toMainMap() {
        NavigationUtil.logD(TAG + "toMainMap");
        mNaviRepository.startMainMapPage();
    }
}
