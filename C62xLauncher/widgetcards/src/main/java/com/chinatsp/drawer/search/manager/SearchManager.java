package com.chinatsp.drawer.search.manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.Log;

import com.chinatsp.drawer.bean.SearchBean;
import com.chinatsp.drawer.search.db.SearchDB;
import com.chinatsp.drawer.search.receiver.AppInstallStatusReceiver;
import com.chinatsp.drawer.search.utils.FileUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchManager {
    private Context mContext;
    private SearchDB db;
    private static final String aospSettings = "com.android.settings";//原生设置
    private static final String launcher = "com.chinatsp.launcher";//luancher
    private static final String CarTrustAgentService = "com.android.car.trust";//CarTrustAgentService
    private static final String AVMCalibration = "com.mediatek.avmcalibration";//AVMCalibration
    private static final String aospFilemanager = "com.android.documentsui";//文件
    private static final String tfactory = "com.chinatsp.tfactoryapp";//工厂设置
    private static final String subscriber = "com.google.android.car.vms.subscriber";//VmsSubscriberClientSample
    private static final String avmDemo = "com.mediatek.avm";//AVMDemo
    private static final String carcorderdemo = "com.mediatek.carcorderdemo";//Carcorder Demo
    private static final String b561Radio = "com.oushang.radio";//b561 Radio
    private static final String applet = "com.iflytek.autofly.applet";//语助业务app

    private static class Holder {
        public static SearchManager manager = new SearchManager();
    }

    private static final String TAG = "SearchManager";

    public static SearchManager getInstance() {
        return SearchManager.Holder.manager;
    }

    public void init(Context context) {
        this.mContext = context;
        initDB();
        //注册监听APP安装卸载广播
        registerAppInstallBroadcast();
    }

    private void initDB() {
        db = new SearchDB(mContext);
        //获取search_data数据
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(FileUtils.getFromAssets(mContext, "search_data.json")).getAsJsonObject();
        //存在数据库表及数据
        if (db.isTableExist() && db.countLocation() > 0) {
            //本地数据
            List<SearchBean> fileList = FileUtils.stringToList(jsonObject.get("Keywords").toString(), SearchBean.class);
            int dbVersion = Integer.parseInt(db.getData().get(0).getDataVersion());
            int fileVersion = Integer.parseInt(fileList.get(0).getDataVersion());
            //数据库版本小于文件版本，就更新
            if (dbVersion < fileVersion) {
                //删除之前数据库
                db.deleteLocation();
                //插入数据库
                Log.d(TAG, "add insertDB");
                insertDB();
            }
        } else {
            Log.d(TAG, "NO tab insertDB");
            if (!db.isTableExist()) {
                db.createSearchTable();
            }
            insertDB();
        }
    }


    /**
     * 获取桌面应用集合
     *
     * @return 返回应用列表集合
     */
    private List<ResolveInfo> getApps() {
        PackageManager packageManager = mContext.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities(i, 0);
    }

    //不显示在桌面的应用名单
    public static List<String> blackListApps = Arrays.asList(
            aospSettings,
            launcher,
            CarTrustAgentService,
            AVMCalibration,
            aospFilemanager,
            tfactory,
            subscriber,
            avmDemo,
            carcorderdemo,
            b561Radio,
            applet
    );

    /**
     * @return 得到最完整的桌面应用、剔除不在桌面的应用
     */
    private List<SearchBean> getDesktopList() {
        List<SearchBean> list = new ArrayList<>();
        List<ResolveInfo> resolveInfoList = getApps();
        for (int i = 0; i < resolveInfoList.size(); i++) {
            if (!blackListApps.contains(resolveInfoList.get(i).activityInfo.packageName)) {
                SearchBean searchBean = new SearchBean();
                searchBean.setModelName("");
                searchBean.setIntentAction(resolveInfoList.get(i).activityInfo.packageName);
                if (FileUtils.getLanguage() == 1) {
                    searchBean.setChineseFunction(resolveInfoList.get(i).activityInfo.loadLabel(mContext.getPackageManager()).toString());
                    searchBean.setEnglishFunction("");
                } else {
                    searchBean.setChineseFunction("");
                    searchBean.setEnglishFunction(resolveInfoList.get(i).activityInfo.loadLabel(mContext.getPackageManager()).toString());
                }
                searchBean.setChineseFunctionLevel("");
                searchBean.setEnglishFunctionLevel("");
                searchBean.setIntentInterface("");
                searchBean.setCarVersion("");
                searchBean.setDataVersion("1");
                list.add(searchBean);
            }
        }

        //单独添加应用管理
        SearchBean searchBean = new SearchBean();
        searchBean.setModelName("");
        searchBean.setIntentAction("com.chinatsp.appmanagement");
        if (FileUtils.getLanguage() == 1) {
            searchBean.setChineseFunction("应用管理");
        } else {
            searchBean.setChineseFunction("");
        }
        searchBean.setChineseFunctionLevel("");
        searchBean.setEnglishFunction("Application management");
        searchBean.setEnglishFunctionLevel("");
        searchBean.setIntentInterface("");
        searchBean.setCarVersion("");
        searchBean.setDataVersion("1");
        list.add(searchBean);
        return list;
    }

    /**
     * 插入数据库
     */
    public void insertDB() {
        int EXPAND_ANIM_DURATION = 200;
        new Handler().postDelayed(() -> {
            // 此处延迟,是为了等待清空表的时候在插入，避免奔溃
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(FileUtils.getFromAssets(mContext, "search_data.json")).getAsJsonObject();
            List<SearchBean> searchBeanList = new ArrayList<>();
            searchBeanList.addAll(getDesktopList());
            searchBeanList.addAll(FileUtils.stringToList(jsonObject.get("Keywords").toString(), SearchBean.class));
            for (int i = 0; i < searchBeanList.size(); i++) {
                db.insertSearch(searchBeanList.get(i));//循环插入数据库
            }
        }, EXPAND_ANIM_DURATION);
    }

    private void registerAppInstallBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        AppInstallStatusReceiver receiver = new AppInstallStatusReceiver();
        mContext.registerReceiver(receiver, intentFilter);
    }

    /**
     * 删除数据库数据
     */
    public void deleteDB() {
        db.deleteLocation();
    }
}
