package com.chinatsp.navigation.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.autonavi.amapauto.jsonsdk.JsonProtocolManager;
import com.autonavi.autoaidlwidget.AutoAidlWidgetManager;
import com.chinatsp.navigation.NaviController;
import com.chinatsp.navigation.gaode.RequestParamCreator;

import launcher.base.ipc.BaseRemoteConnector;
import launcher.base.ipc.BaseRepository;
import launcher.base.ipc.IRemoteDataCallback;

public class NaviRepository extends BaseRepository {
    private NaviRepository() {

    }



    private static class Holder{
        private static NaviRepository instance = new NaviRepository();
    }

    public static NaviRepository getInstance() {
        return Holder.instance;
    }

    private JsonProtocolManager jsonProtocolManager = JsonProtocolManager.getInstance();

    @Override
    protected NaviRemoteConnector createRemoteConnector(Context context) {
        return new NaviRemoteConnector(new NaviRemoteProxy());
    }

    public void getLocation() {
        jsonProtocolManager.request(new RequestParamCreator().createMyLocation());
    }

    public void getNavigationStatus() {
        jsonProtocolManager.request(new RequestParamCreator().createNavigationStatus());
    }

    public void startSearchPage() {
    }

    public void startNaviToCompanyPage() {
        // 1: 去公司
        jsonProtocolManager.request(new RequestParamCreator().createNavigationToHomeOrCompany(1));
    }

    public void startNaviToHomePage() {
        // 0: 回家
        jsonProtocolManager.request(new RequestParamCreator().createNavigationToHomeOrCompany(0));

    }
}
