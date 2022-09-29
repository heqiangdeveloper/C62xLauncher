package com.chinatsp.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.content.res.AppCompatResources;

import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.gaode.bean.Address;
import com.chinatsp.navigation.gaode.bean.GaoDeResponse;
import com.chinatsp.navigation.gaode.bean.NavigationStatus;
import com.chinatsp.navigation.gaode.bean.RoadInfo;
import com.chinatsp.navigation.repository.INaviCallback;
import com.chinatsp.navigation.repository.NaviRepository;
import com.chinatsp.navigation.repository.ResponseParser;


import launcher.base.ipc.IConnectListener;
import launcher.base.ipc.IRemoteDataCallback;

public class NaviController implements INaviCallback {
    private NaviCardView mView;
    private String TAG = "NaviController ";
    public static final int STATE_FREE = 0;
    public static final int STATE_IN_NAVIGATION = 1;
    private int mState = STATE_FREE;
    private boolean isInNavigation; // 是否在导航状态中
    private NaviRepository mNaviRepository = NaviRepository.getInstance();
    private Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public NaviController(NaviCardView view) {
        mView = view;
        Context context = view.getContext();
        mNaviRepository.init(context);

        initAidlWidgetManager(context);
        mNaviRepository.registerDataCallback(mIRemoteDataCallback);
        mNaviRepository.registerConnectListener(mConnectListener);
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
        mState = STATE_FREE;
        mView.refreshState(mState);

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
        if (!isInNavigation) {
            if (address != null) {
                mView.refreshMyLocation(address.getPoiName());
            }
        }
    }

    @Override
    public void receiveNavigationStatus(GaoDeResponse<NavigationStatus> gaoDeResponse) {
        NavigationStatus status = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveNavigationStatus status:" + status);
        if (status != null) {
            isInNavigation = (status.getStatus() == NavigationStatus.STATUS_IN_NAVIGATION);
        }
    }

    @Override
    public void receiveCurRoadInfo(GaoDeResponse<RoadInfo> gaoDeResponse) {
        RoadInfo roadInfo = gaoDeResponse.getData();
        NavigationUtil.logD(TAG + "receiveCurRoadInfo roadInfo:" + roadInfo);
        if (!isInNavigation) {
            if (roadInfo != null) {
                mView.refreshMyLocation(roadInfo.getCurRoadName());
            }
        }
    }

    public void startSearch() {
        mNaviRepository.startSearchPage();
    }

    public void naviToCompany() {
        mNaviRepository.startNaviToCompanyPage();
    }

    public void naviToHome() {
        mNaviRepository.startNaviToHomePage();
    }
}
