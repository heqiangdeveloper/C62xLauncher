package com.chinatsp.navigation.repository;

import android.os.Handler;
import android.os.Looper;

import com.chinatsp.navigation.NavigationUtil;
import com.chinatsp.navigation.gaode.ProtocolIds;
import com.chinatsp.navigation.gaode.ProtocolKeys;
import com.chinatsp.navigation.gaode.bean.GaoDeResponse;
import com.chinatsp.navigation.gaode.bean.ResponseConvert;

import org.json.JSONException;
import org.json.JSONObject;


import launcher.base.async.AsyncSchedule;

public class ResponseParser {
    private JSONObject mJSONObject;
    private int mProtocolId;
    private String originMessage;
    private INaviCallback mCallback;
    private String TAG = "ResponseParser ";
    private Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public ResponseParser(String message, INaviCallback callback) {
        this.originMessage = message;
        mCallback = callback;
    }

    public void parse() {
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                GaoDeResponse gaoDeResponse = null;
                try {
                    mJSONObject = new JSONObject(originMessage);
                    mProtocolId = mJSONObject.optInt(ProtocolKeys.PROTOCOL_ID);
                    gaoDeResponse = new ResponseConvert<>().convertFromJson(originMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GaoDeResponse finalGaoDeResponse = gaoDeResponse;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dispatch(finalGaoDeResponse);
                    }
                });
            }
        });
    }

    private void dispatch(GaoDeResponse gaoDeResponse) {
        if (mCallback == null) {
            return;
        }
        NavigationUtil.logI(TAG + "dispatch :" + mProtocolId + "\nBody: " + mJSONObject);
        switch (mProtocolId) {
            case ProtocolIds.MY_LOCATION:
                mCallback.receiveMyLocation(gaoDeResponse);
                break;
            case ProtocolIds.NAVIGATION_STATUS:
                mCallback.receiveNavigationStatus(gaoDeResponse);
                break;
            case ProtocolIds.CURRENT_ROAD_NAME:
                mCallback.receiveCurRoadInfo(gaoDeResponse);
                break;
            case ProtocolIds.NAVI_GUIDE_INFO:
                mCallback.receiveNaviGuideInfo(gaoDeResponse);
                break;
        }
    }

}
