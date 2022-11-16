package com.chinatsp.apppanel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.ClassifyView;
import com.anarchy.classifyview.adapter.MainRecyclerViewCallBack;
import com.anarchy.classifyview.event.AppInstallStatusEvent;
import com.anarchy.classifyview.event.ChangeSubTitleEvent;
import com.anarchy.classifyview.event.ChangeTitleEvent;
import com.anarchy.classifyview.event.Event;
import com.anarchy.classifyview.event.HideSubContainerEvent;
import com.anarchy.classifyview.event.JumpToCardEvent;
import com.anarchy.classifyview.event.ReStoreDataEvent;
import com.anarchy.classifyview.listener.SoftKeyBoardListener;
import com.anarchy.classifyview.util.L;
import com.anarchy.classifyview.util.MyConfigs;
import launcher.base.applists.AppLists;
import com.chinatsp.apppanel.AppConfigs.Priorities;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.adapter.AddAppAdapter;
import com.chinatsp.apppanel.adapter.MyAppInfoAdapter;
import com.chinatsp.apppanel.db.MyAppDB;
import com.chinatsp.apppanel.event.CancelDownloadEvent;
import com.chinatsp.apppanel.event.DownloadEvent;
import com.chinatsp.apppanel.event.FailDownloadEvent;
import com.chinatsp.apppanel.event.InstalledAnimEndEvent;
import com.chinatsp.apppanel.event.NotRemindEvent;
import com.chinatsp.apppanel.event.StartDownloadEvent;
import com.chinatsp.apppanel.event.UninstallCommandEvent;
import com.chinatsp.apppanel.event.UpdateEvent;
import com.huawei.appmarket.launcheragent.launcher.AppState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import launcher.base.async.AsyncSchedule;
import launcher.base.service.AppServiceManager;
import launcher.base.service.car.ICarService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAppFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "MyAppFragment";
    private TextView loadingTv;
    private ClassifyView appInfoClassifyView;
    private AddAppAdapter adapter;
    private MyAppDB db;
    private LocationBean locationBean;
    private ByteArrayOutputStream baos;
    private Bitmap bitmap;
    private Drawable drawable;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private MyAppInfoAdapter mMyAppInfoAdapter;
    private List<List<LocationBean>> data;
    private static boolean isStoringData = false;
    private List<String> canUninstallNameLists = new ArrayList<>();
    private boolean isNeedSort = false;//是否需要排序
    private List<String> downloadPkgs = new ArrayList<>();
    private String versionCode = "";
    private PackageManager pm;
    private PackageInfo pi;
    private int subParentIndex = -1;//sub所在的主位置
    public MyAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAppFragment newInstance(String param1, String param2) {
        MyAppFragment fragment = new MyAppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = new MyAppDB(getContext());
        preferences = getContext().getSharedPreferences(MyConfigs.APPPANELSP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        pm = getContext().getPackageManager();
        resetMainDeleteFlag(false);
        resetSubDeleteFlag(false);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_app, container, false);
        loadingTv = (TextView) view.findViewById(R.id.loading_tv);
        loadingTv.setVisibility(View.VISIBLE);
        appInfoClassifyView = (ClassifyView) view.findViewById(R.id.classify_view);
        //adapter = new AppInfoAdapter(getContext(),getApps());
        data = new ArrayList<>();

        LocationBean locationBean = null;
        Log.d(TAG,"db.countLocation() = " + db.countLocation());
        if(db.countLocation() == 0){//没有数据记录
            isNeedSort = true;
            getOriginalData();
        }else {
            isNeedSort = false;
            data = db.getData1();
            Log.d(TAG,"data.size = " + data.size());
            if(data.size() == 0){
                isNeedSort = true;
                getOriginalData();
            }
        }

        addPushInstalledApp();//添加通过push方式安装的应用
        deleteUninstallApp();//删除掉未安装的应用，应用管理除外
        checkDVR();//检查车型DVR
        if(isNeedSort){//首次使用，需要按默认顺序排序
            sortWithPriority();
        }
        addDownloadApps();//添加正在下载的apps
        //更新应用名称，与系统语言保持一致
        refreshAppName();
        //更新应用图标
        refreshIcon();
        loadingTv.setVisibility(View.GONE);
        mMyAppInfoAdapter = new MyAppInfoAdapter(view.getContext(), data);
        appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
//        appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {//键盘显示
                appInfoClassifyView.setSoftKeyBoardStatus(true);
            }

            @Override
            public void keyBoardHide(int height) {//键盘隐藏
                appInfoClassifyView.setSoftKeyBoardStatus(false);
            }
        });
        return view;
    }

    private List<String> getCanUninstallLists(){
        if(canUninstallNameLists != null) canUninstallNameLists.clear();
        for(List<LocationBean> lists:data){
            if(lists != null){
                for(int i = 0; i < lists.size(); i++){
                    locationBean = lists.get(i);
                    if(locationBean != null){
                        if(!AppLists.APPMANAGEMENT.equals(locationBean.getPackageName()) &&
                                !AppLists.isSystemApplication(getContext(),locationBean.getPackageName())){
                            if(!canUninstallNameLists.contains(locationBean.getName())){
                                canUninstallNameLists.add(locationBean.getName());
                                Log.d(TAG,"add in CanUninstallLists: " + locationBean.getName());
                            }
                        }
                    }
                }
            }
        }
        return canUninstallNameLists;
    }

    private void getOriginalData(){
        Log.d("hqtest","getOriginalData");
        List<ResolveInfo> allApps = getApps();
        allApps = getAvailabelApps(allApps);
        allApps = removeRepeatApps(allApps);
        //此处第一个位置留给应用管理
        allApps.add(0,null);
        ResolveInfo info;
        int num = 0;
        for(int i = 0; i < allApps.size();i++){
            //L.d("name: " + info.activityInfo.loadLabel(getContext().getPackageManager()) + "," + info.activityInfo.packageName);
            List<LocationBean> inner = new ArrayList<>();
            info = allApps.get(i);
            locationBean = new LocationBean();
            locationBean.setParentIndex(i);
            locationBean.setChildIndex(-1);
            if(info == null){//说明是应用管理，特殊处理
                Log.d(TAG,"command appmanagement");
                locationBean.setPackageName(AppLists.APPMANAGEMENT);
                //drawable = getResources().getDrawable(R.mipmap.ic_appmanagement_new);
                drawable = AppLists.getResId(getContext(),locationBean.getPackageName());
                locationBean.setName(getResources().getString(R.string.appmanagement_name));
            }else {
                locationBean.setPackageName(info.activityInfo.packageName);
                drawable = info.activityInfo.loadIcon(getContext().getPackageManager());
                locationBean.setName((info.activityInfo.loadLabel(getContext().getPackageManager())).toString());
            }
            locationBean.setPriority(getPriority(locationBean.getPackageName()));
            locationBean.setCanuninstalled(AppLists.isSystemApplication(getContext(),locationBean.getPackageName()) ? 0:1);
            locationBean.setInstalled(AppState.INSTALLED_COMPLETELY);
            locationBean.setTitle("");
            locationBean.setImgByte(null);
            locationBean.setImgDrawable(drawable);
            try {
                pi = pm.getPackageInfo(locationBean.getPackageName(), 0);
                versionCode = String.valueOf(pi.versionCode);
            }catch (Exception e){
                versionCode = "";
            }
            locationBean.setReserve1(versionCode);
            locationBean.setReserve2(versionCode);

            num = db.isExistPackage(locationBean.getPackageName());
            if(num == 0){
                db.insertLocation(locationBean);
            }else {
                db.updateIndex(locationBean);
            }
            inner.add(locationBean);
            data.add(inner);
        }
    }

    /*
    *  删除掉未安装的应用，应用管理除外,要排除还在下载中的情况
     */
    private void deleteUninstallApp(){
        List<ResolveInfo> allApps = getApps();
        for (int i = 0; i < allApps.size(); i++){
            //如果应用没有默认的MainActivity也不能点进去
            if(TextUtils.isEmpty(allApps.get(i).activityInfo.name)){
                allApps.remove(i);
                i--;
            }
        }
        List<String> packageLists = getInstalledPackages(allApps);

        for(int i = 0; i < data.size(); i++){
            List<LocationBean> list = data.get(i);
            for(int j = 0; j < list.size(); j++){
                String pkgName = list.get(j).getPackageName();
                int installed = list.get(j).getInstalled();
                if(!pkgName.equals(AppLists.APPMANAGEMENT) && !packageLists.contains(pkgName) &&
                        (installed == AppState.INSTALLED || installed == AppState.COULD_UPDATE ||
                                installed == AppState.INSTALLED_COMPLETELY)){
                    AsyncSchedule.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.deleteLocation(pkgName);
                        }
                    });
                    data.get(i).remove(j);
                    j--;
                }
            }
        }

        //清除掉子list长度为0的
        for(int k = 0; k < data.size(); k++){
            if(data.get(k).size() == 0){
                data.remove(k);
                k--;
            }
        }
    }

    /*
    *  添加通过push方式安装的应用
     */
    private void addPushInstalledApp(){
        //获取data中所有的包名
        List<String> pkgList = new ArrayList<>();
        String pkgName = "";
        for(int i = 0; i < data.size(); i++){
            List<LocationBean> list = data.get(i);
            for(int j = 0; j < list.size(); j++){
                pkgList.add(list.get(j).getPackageName());
            }
        }

        List<ResolveInfo> allApps = getApps();
        allApps = getAvailabelApps(allApps);
        allApps = removeRepeatApps(allApps);
        //处理系统安装的应用
        for(int i = 0; i < allApps.size(); i++){
            pkgName = allApps.get(i).activityInfo.packageName;
            if(!pkgList.contains(pkgName)){
                List<LocationBean> inner = new ArrayList<>();
                locationBean = new LocationBean();
                locationBean.setParentIndex(data.size());
                locationBean.setChildIndex(-1);

                locationBean.setPackageName(pkgName);
                if(allApps.get(i).activityInfo.loadIcon(getContext().getPackageManager()) != null){
                    locationBean.setImgDrawable(allApps.get(i).activityInfo.loadIcon(getContext().getPackageManager()));
                    locationBean.setName(allApps.get(i).activityInfo.loadLabel(getContext().getPackageManager()).toString());
                    locationBean.setInstalled(AppState.INSTALLED_COMPLETELY);
                    locationBean.setCanuninstalled(AppLists.isSystemApplication(getContext(),locationBean.getPackageName()) ? 0:1);
                    locationBean.setTitle("");
                    locationBean.setImgByte(null);
                    try {
                        pi = pm.getPackageInfo(locationBean.getPackageName(), 0);
                        versionCode = String.valueOf(pi.versionCode);
                    }catch (Exception e){
                        versionCode = "";
                    }
                    locationBean.setReserve1(versionCode);
                    locationBean.setReserve2(versionCode);

                    int num = db.isExistPackage(locationBean.getPackageName());
                    if(num == 0){
                        AsyncSchedule.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.insertLocation(locationBean);
                            }
                        });
                    }else {
                        AsyncSchedule.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.updateIndex(locationBean);
                            }
                        });
                    }
                    inner.add(locationBean);
                    data.add(inner);
                }
            }
        }
    }

    /*
    *  判断有无安装dvr
     */
    private void checkDVR(){
        ICarService carService = (ICarService) AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        if(carService.isHasDVR()){
            //有DVR，addPushInstalledApp中已处理
        }else {//无DVR
            A: for(List<LocationBean> lists:data){
                if(lists != null && lists.size() < 2 && lists.get(0) != null &&
                        AppLists.dvr.equals(lists.get(0).getPackageName())){
                    data.remove(lists);
                    AsyncSchedule.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.deleteLocation(AppLists.dvr);
                        }
                    });
                    break A;
                }else if(lists != null && lists.size() >= 2){
                    for(LocationBean item : lists){
                        if(AppLists.dvr.equals(item.getPackageName())){
                            lists.remove(item);
                            AsyncSchedule.execute(new Runnable() {
                                @Override
                                public void run() {
                                    db.deleteLocation(AppLists.dvr);
                                }
                            });
                            break A;
                        }
                    }
                }
            }
        }
    }

    private int getPriority(String pkgName){
        if(pkgName.equals(AppLists.systemSettings)){
            return Priorities.systemSettings;
        }else if(pkgName.equals(AppLists.vehicleSettings)){
            return Priorities.vehicleSettings;
        }else if(pkgName.equals(AppLists.media)){
            return Priorities.media;
        }else if(pkgName.equals(AppLists.usercenter)){
            return Priorities.usercenter;
        }else if(pkgName.equals(AppLists.btPhone)){
            return Priorities.btPhone;
        }else if(pkgName.equals(AppLists.iot)){
            return Priorities.iot;
        }else if(pkgName.equals(AppLists.dvr)){
            return Priorities.dvr;
        }else if(pkgName.equals(AppLists.ifly)){
            return Priorities.ifly;
        }else if(pkgName.equals(AppLists.userbook)){
            return Priorities.userbook;
        }else if(pkgName.equals(AppLists.appmarket)){
            return Priorities.appmarket;
        }else if(pkgName.equals(AppLists.APPMANAGEMENT)){
            return Priorities.appmanagement;
        }else if(pkgName.equals(AppLists.iquting)){
            return Priorities.iquting;
        }else if(pkgName.equals(AppLists.volcano)){
            return Priorities.volcano;
        }else if(pkgName.equals(AppLists.amap)){
            return Priorities.amap;
        }else if(pkgName.equals(AppLists.easyconn)){
            return Priorities.easyconn;
        }else if(pkgName.equals(AppLists.weather)){
            return Priorities.weather;
        }else {
            return Priorities.MIN_PRIORITY;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event){
        if(event instanceof ChangeTitleEvent){
            //重置delete标签
            resetSubDeleteFlag(false);
            changeTitle((ChangeTitleEvent)event);
        }else if(event instanceof HideSubContainerEvent){
            //重置delete标签
            resetSubDeleteFlag(false);
            appInfoClassifyView.hideSubContainer();
        }else if(event instanceof AppInstallStatusEvent){
            //重置delete标签
            //resetDeleteFlag(false,-1);
            int status = ((AppInstallStatusEvent) event).getStatus();
            String packageName = ((AppInstallStatusEvent) event).getPackageName();
            Log.d(TAG,"status = " + status + ",pacakageName is: " + packageName);
            if(status == 1){//安装
                resetMainDeleteFlag(false);
                data = db.getData1();
                if(data.size() == 0){
                    getOriginalData();
                }
                //更新应用图标
                refreshIcon();
                mMyAppInfoAdapter = new MyAppInfoAdapter(getContext(), data);
                appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
//                appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());
            }else {//卸载
                if(!AppLists.isInBlackListApp(packageName)){
                    List<LocationBean> lists;
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    Log.d(TAG,"isSubShow = " + isSubShow);
                    A:for(int k = 0; k < data.size(); k++){
                        lists = data.get(k);
                        if(lists == null) continue;//如果是 添加按钮，跳过
                        //lists.removeAll(Collections.singleton(null));//清除掉null对象
                        for(int i = 0; i < lists.size(); i++) {
                            locationBean = lists.get(i);
                            if(locationBean == null) continue;
                            if (packageName.equals(locationBean.getPackageName())) {
                                if(lists.size() == 1){//如果是main
                                    data.remove(lists);
                                    if(isSubShow){
                                        appInfoClassifyView.hideSubContainer();
                                    }
                                }else if(lists.size() <= 3){//如果在sub中，删除当前的后，隐藏sub
                                    lists.remove(locationBean);
                                    lists.removeAll(Collections.singleton(null));//清除掉null对象
                                    if(isSubShow){
                                        appInfoClassifyView.hideSubContainer();
                                    }
                                }else {
                                    lists.remove(locationBean);
                                }

                                //防止整个页面都刷新，不重新绑定，调用notifyDataSetChanged
                                //mMyAppInfoAdapter = new MyAppInfoAdapter(getContext(), data);
                                //appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
                                mMyAppInfoAdapter.notifyDataSetChanged();
                                if(isSubShow){
                                    mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                                }
//                                appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());

                                AsyncSchedule.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.deleteLocation(packageName);
                                    }
                                });
                                break A;
                            }
                        }
                    }
                }
            }
        }else if(event instanceof ReStoreDataEvent){
            if(!isStoringData) storeData();
        }else if(event instanceof UninstallCommandEvent) {//倒计时退出编辑的事件
            Log.d("CountTimer", "UninstallCommandEvent start count");
            appInfoClassifyView.startCountTimer();
        } else if(event instanceof StartDownloadEvent){//开始下载事件
            Log.d("DownloadEvent","StartDownloadEvent");
            locationBean = ((StartDownloadEvent) event).getLocationBean();
            String pkgName = locationBean.getPackageName();

            if(data != null && data.size() != 0){
                List<LocationBean> lists;
                LocationBean mLocationBean;
                int k;
                //如果data中有此下载的应用
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++){
                        mLocationBean = lists.get(i);
                        if(mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())){
                            //do nothing
                            Log.d("DownloadEvent","StartDownloadEvent data already has " + pkgName);
                            break A;
                        }
                    }
                }

                //说明data中还未有此下载的应用
                if(k >= data.size()){
                    Log.d("DownloadEvent","StartDownloadEvent data not has " + pkgName);
                    locationBean.setParentIndex(data.size());
                    locationBean.setChildIndex(-1);

                    List<LocationBean> inner = new ArrayList<>();
                    inner.add(locationBean);
                    data.add(inner);
                    mMyAppInfoAdapter.notifyItemChanged(data.size());
//                    appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());

                    int num = db.isExistPackage(locationBean.getPackageName());
                    if(num == 0){
                        db.insertLocation(locationBean);
                    }else {
                        db.updateLocation(locationBean);
                    }
                }
            }
        }else if(event instanceof DownloadEvent){//下载事件
            Log.d("DownloadEvent","DownloadEvent");
            locationBean = ((DownloadEvent) event).getLocationBean();
            int appState = locationBean.getInstalled();
            String pkgName = locationBean.getPackageName();
            if(data != null && data.size() != 0){
                //刷新该应用桌面状态
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            Log.d("DownloadEvent",pkgName + " download status " + locationBean.getStatus());
                            mLocationBean.setName(locationBean.getName());
                            mLocationBean.setInstalled(locationBean.getInstalled());
                            mLocationBean.setStatus(locationBean.getStatus());
                            mLocationBean.setReserve1(locationBean.getReserve1());
                            mLocationBean.setReserve2(locationBean.getReserve2());
                            break A;
                        }
                    }
                }
                //data中存在此应用
                if(k < data.size()){
                    mMyAppInfoAdapter.notifyItemChanged(k);
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    //确认是否是当前正在下载的那个sub
                    if((subParentIndex == k) && isSubShow && lists != null && lists.size() >= 3){
                        mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                    }


                    int num = db.isExistPackage(pkgName);
                    if(num == 0){
                        db.insertLocation(mLocationBean);
                    }else {
                        db.updateDownloadStatusInLocation(mLocationBean);
                    }
                    if(appState == AppState.INSTALLED || appState == AppState.INSTALLED_COMPLETELY){
                        //安装后删除download表中的数据
                        db.deleteDownload(pkgName);
                    }
                }
            }
        }else if(event instanceof CancelDownloadEvent){//取消下载
            String packageName = ((CancelDownloadEvent) event).getPackageName();
            if(data != null && data.size() != 0){
                //刷新该应用桌面状态
                List<LocationBean> lists;
                LocationBean mLocationBean;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && packageName.equals(mLocationBean.getPackageName())) {
                            //发送已经卸载事件
                            EventBus.getDefault().post(new AppInstallStatusEvent(0,packageName));
                            break A;
                        }
                    }
                }

                if(db.isExistPackage(packageName) != 0){
                    db.deleteLocation(packageName);
                }

                if(db.isExistPackageInDownload(packageName) != 0){
                    db.deleteDownload(packageName);
                }
            }
        }else if(event instanceof UpdateEvent){//更新
            Log.d("DownloadEvent","UpdateEvent");
            locationBean = ((UpdateEvent) event).getLocationBean();
            String pkgName = locationBean.getPackageName();
            if(data != null && data.size() != 0){
                //刷新该应用桌面状态
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            mLocationBean.setInstalled(locationBean.getInstalled());
                            mLocationBean.setReserve3(locationBean.getReserve3());
                            break A;
                        }
                    }
                }

                //data中存在此应用
                if(k < data.size()){
                    mMyAppInfoAdapter.notifyItemChanged(k);
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    if((subParentIndex == k) && isSubShow && lists != null && lists.size() >= 3){
                        mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                    }

                    int num = db.isExistPackage(pkgName);
                    if(num == 0){
                        db.insertLocation(mLocationBean);
                    }else {
                        db.updateDownloadStatusInLocation(mLocationBean);
                    }
                    //删除download表中的数据
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof NotRemindEvent){//可更新时 不再提醒事件
            Log.d("DownloadEvent","NotRemindEvent");
            String reverse3 = ((NotRemindEvent) event).getReverse3();
            String pkgName = ((NotRemindEvent) event).getPackageName();
            if(data != null && data.size() != 0){
                //刷新该应用桌面状态
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            //将不再提醒时的版本号 设置为与 待更新的版本号一致
                            mLocationBean.setReserve2(reverse3);
                            break A;
                        }
                    }
                }

                //data中存在此应用
                if(k < data.size()){
                    mMyAppInfoAdapter.notifyItemChanged(k);
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    if((subParentIndex == k) && isSubShow && lists != null && lists.size() >= 3){
                        mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                    }

                    int num = db.isExistPackage(pkgName);
                    if(num == 0){
                        db.insertLocation(mLocationBean);
                    }else {
                        db.updateDownloadStatusInLocation(mLocationBean);
                    }
                    //删除download表中的数据
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof FailDownloadEvent){//下载失败
            Log.d("DownloadEvent","FailDownloadEvent");
            String pkgName = ((FailDownloadEvent) event).getLocationBean().getPackageName();
            int installed =  ((FailDownloadEvent) event).getLocationBean().getInstalled();
            if(data != null && data.size() != 0){
                //刷新该应用桌面状态
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//如果是 添加按钮，跳过
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            mLocationBean.setInstalled(installed);
                            break A;
                        }
                    }
                }

                //data中存在此应用
                if(k < data.size()){
                    mMyAppInfoAdapter.notifyItemChanged(k);
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    if((subParentIndex == k) && isSubShow && lists != null && lists.size() >= 3){
                        mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                    }

                    int num = db.isExistPackage(pkgName);
                    if(num == 0){
                        db.insertLocation(mLocationBean);
                    }else {
                        db.updateInstalledInLocation(mLocationBean);
                    }
                    //删除download表中的数据
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof InstalledAnimEndEvent){//安装完成的事件
            Log.d("DownloadEvent","InstalledAnimEndEvent");
            List<String> installedPackages = ((InstalledAnimEndEvent) event).getInstalledPackages();
            if(installedPackages != null){
                if(data != null && data.size() != 0){
                    //刷新该应用桌面状态
                    List<LocationBean> lists = null;
                    LocationBean mLocationBean = null;
                    int k;
                    boolean isSubShow;
                    int num;
                    A:for(k = 0; k < data.size(); k++) {
                        lists = data.get(k);
                        if (lists == null) continue;//如果是 添加按钮，跳过
                        for(int i = 0; i < lists.size(); i++) {
                            mLocationBean = lists.get(i);
                            if (mLocationBean != null && installedPackages.contains(mLocationBean.getPackageName())) {
                                mLocationBean.setInstalled(AppState.INSTALLED_COMPLETELY);

                                mMyAppInfoAdapter.notifyItemChanged(k);
                                isSubShow = appInfoClassifyView.isSubContainerShow();
                                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                                if((k == subParentIndex) && isSubShow && lists != null && lists.size() >= 3){
                                    mMyAppInfoAdapter.getSubAdapter().initData(k,lists);
                                }
                                num = db.isExistPackage(mLocationBean.getPackageName());
                                if(num != 0){
                                    db.updateInstalledInLocation(mLocationBean);
                                }
                            }
                        }
                    }
                }
            }
        }else if(event instanceof JumpToCardEvent){
            Intent intent = new Intent();
            intent.setClassName("com.chinatsp.launcher","com.chinatsp.launcher.CarLauncher");
            getContext().startActivity(intent);
        }else if(event instanceof ChangeSubTitleEvent){
            boolean isSubShow = appInfoClassifyView.isSubContainerShow();
            String title = ((ChangeSubTitleEvent)event).getTitle();
            int position = ((ChangeSubTitleEvent)event).getPosition();
            Log.d(TAG,"position = " + position + ",title = " + title);
            if(isSubShow){
                if(appInfoClassifyView.titleTv != null) appInfoClassifyView.titleTv.setText(title);
            }
        }
    }

    public void changeTitle(ChangeTitleEvent event){
        L.d("changeTile to " + event.getTitle());
        List<LocationBean> infos = data.get(event.getParentIndex());
        String newTitle = getNewTitle(event.getParentIndex(),event.getTitle());
        for(LocationBean locationBean : infos){
            if(locationBean != null){
                locationBean.setTitle(newTitle);
                db.updateTitle(locationBean);
            }
        }
        mMyAppInfoAdapter.notifyDataSetChanged();
    }

    private List<String> titleLists = new ArrayList<>();
    private String getNewTitle(int index,String title){
        List<LocationBean> lists;
        titleLists.clear();
        A:for(int i = 0; i < data.size(); i++){
            lists = data.get(i);
            if(i != index && lists != null && lists.size() > 1){
                titleLists.add(lists.get(0).getTitle());
            }
        }

        titleLists = titleLists.stream().distinct().collect(Collectors.toList());//去掉重复的
        if(titleLists.contains(title)){
            Pattern pattern = Pattern.compile("\\d+$");
            Matcher matcher = pattern.matcher(title);

            int num = 1;
            if(matcher.find()){
                String s = matcher.group();
                Log.d(TAG,"字符串 " + title + " 是以数字结尾的，结尾的数字是：" + s);
                title = title.replace(s,"");
                num = Integer.parseInt(s);
            } else{
                Log.d(TAG,"字符串" + title + "不是以数字结尾的");
            }

            while (titleLists.contains(title + num)){
                num++;
            }
            title = title + num;
        }
        return title;
    }

    private void resetMainDeleteFlag(boolean isShowDelete){
        editor.putBoolean(MyConfigs.MAINSHOWDELETE,isShowDelete);
        editor.commit();
    }

    private void resetSubDeleteFlag(boolean isShowDelete){
        editor.putBoolean(MyConfigs.SHOWDELETE,isShowDelete);
        editor.commit();
    }

    /**
     * 获取系统中所有的APP
     * @return
     */
    private List<ResolveInfo> getApps(){
        PackageManager packageManager = getContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities(i,0);
    }

    private String getAppName(String pkgName){
        String name = "";
        if(TextUtils.isEmpty(pkgName)){
            name = "";
        }else if(pkgName.equals(AppLists.APPMANAGEMENT)) {
            name = getResources().getString(R.string.appmanagement_name);
        }else {
            List<ResolveInfo> allApps = getApps();
            A:for(int i = 0; i < allApps.size(); i++){
                if(pkgName.equals(allApps.get(i).activityInfo.packageName)){
                    name = (allApps.get(i).activityInfo.loadLabel(getContext().getPackageManager())).toString();
                    break A;
                }
            }
        }
        return name;
    }

    /*
    *  剔除黑名单中的APP
     */
    private List<ResolveInfo> getAvailabelApps(List<ResolveInfo> allApps){
        for (String packages:AppLists.blackListApps) {
            A:for (int i = 0; i < allApps.size(); i++){
                if(packages.equals(allApps.get(i).activityInfo.packageName)){
                    allApps.remove(i);
                    //修复： 部分应用由于集成了别的应用的aar，而aar中manifest又声明了主Activity,导致桌面上会有2个图标
                    //break A;
                    i--;
                }
            }
        }
        return allApps;
    }

    /*
    *  剔除重复的APP
     */
    private List<ResolveInfo> removeRepeatApps(List<ResolveInfo> allApps){
//        List<String> packageLists = new ArrayList<>();
//        for(int i = 0; i < allApps.size(); i++){
//            packageLists.add(allApps.get(i).activityInfo.packageName);
//        }
//        packageLists = packageLists.stream().distinct().collect(Collectors.toList());//去掉重复的包名
        List<String> packageLists = getInstalledPackages(allApps);

        List listTemp = new ArrayList();
        String packageName = "";
        for(int i = 0; i < allApps.size(); i++){
            packageName = allApps.get(i).activityInfo.packageName;
            if(packageLists.contains(packageName)){
                listTemp.add(allApps.get(i));
                packageLists.remove(packageName);
            }
        }
        return listTemp;
    }

    /*
    * 获取系统中所有的已安装的包名
     */
    private List<String> getInstalledPackages(List<ResolveInfo> list){
        List<String> packageLists = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            packageLists.add(list.get(i).activityInfo.packageName);
        }
        packageLists = packageLists.stream().distinct().collect(Collectors.toList());//去掉重复的包名
        return packageLists;
    }

    /*
    *  All app默认排序
    *  系统设置，车辆设置，多媒体，个人中心，电话，行车顾问，行车记录仪，语音，电子说明书，华为应用商城，应用管理，爱趣听，火山车娱，
    *  高德导航，亿连，天气
    */
    private void sortWithPriority(){
        Collections.sort(data, new Comparator<List<LocationBean>>() {
            @Override
            public int compare(List<LocationBean> o1, List<LocationBean> o2) {
                //0代表相等，1表示大于，-1表示小于
                int i = o1.get(0).getPriority() - o2.get(0).getPriority();
                return i;
            }
        });

        //更新存储数据的工作在onResume()中进行
    }

    /*
    * 添加正在下载的apps
     */
    private void addDownloadApps(){
        List<LocationBean> downloadLists = db.getDownloadData();
        List<LocationBean> inner;
        List<LocationBean> dataLists;
        LocationBean mLocationBean;
        String pkgName;
        for(int i = 0; i < downloadLists.size(); i++){
            locationBean = downloadLists.get(i);
            pkgName = locationBean.getPackageName();

            //如果data中有此下载的应用
            int k;
            A:for(k = 0; k < data.size(); k++) {
                dataLists = data.get(k);
                if (dataLists == null) continue;//如果是 添加按钮，跳过
                for(int j = 0; j < dataLists.size(); j++){
                    mLocationBean = dataLists.get(j);
                    if(mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())){
                        if(locationBean.getInstalled() == AppState.COULD_UPDATE){
                            mLocationBean.setInstalled(locationBean.getInstalled());
                            mLocationBean.setReserve3(locationBean.getReserve3());
                        }else {
                            mLocationBean.setInstalled(locationBean.getInstalled());
                            mLocationBean.setName(locationBean.getName());
                            mLocationBean.setStatus(locationBean.getStatus());
                            mLocationBean.setReserve1(locationBean.getReserve1());
                            mLocationBean.setReserve2(locationBean.getReserve2());
                            mLocationBean.setReserve3(locationBean.getReserve3());
                        }
                        break A;
                    }
                }
            }
            //说明data中还未有此下载的应用
            if(k >= data.size()){
                locationBean.setParentIndex(data.size());
                locationBean.setChildIndex(-1);
                inner = new ArrayList<>();
                inner.add(locationBean);
                data.add(inner);
            }
            //onResume的时候会存储至location表

            int isInstalled = locationBean.getInstalled();
            if(isInstalled == AppState.INSTALLED || isInstalled == AppState.INSTALLED_COMPLETELY){
                db.deleteDownload(pkgName);
            }
        }
    }

    /*
    * 更新应用名称，与系统语言保持一致
     */
    private void refreshAppName(){
        for(List<LocationBean> lists:data){
            if(lists != null){
                for(int i = 0; i < lists.size(); i++){
                    locationBean = lists.get(i);
                    if(locationBean != null){
                        locationBean.setName(getAppName(locationBean.getPackageName()));
                    }
                }
            }
        }
    }

    /*
    * 更新应用图标
     */
    private void refreshIcon(){
        for(List<LocationBean> lists:data){
            if(lists != null){
                for(int i = 0; i < lists.size(); i++){
                    locationBean = lists.get(i);
                    if(locationBean != null){
                        drawable = AppLists.getResId(getContext(),locationBean.getPackageName());
                        if(drawable != null){//非第三方应用
                            locationBean.setImgDrawable(drawable);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("heqq","myAppFragment onResume");
        storeData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("heqq","myAppFragment onPause");
        storeData();
    }

    public void storeData(){
        Log.d(TAG,"storeData");
        isStoringData = true;
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                //先清空数据库，再保存桌面布局数据到数据库
//                if(db.countLocation() != 0){
//                    db.deleteLocation();
//                }

                MainRecyclerViewCallBack mainAdapter = (MainRecyclerViewCallBack) appInfoClassifyView.getMainRecyclerView().getAdapter();
                Log.d("heqq","is MainRecyclerViewCallBack");

                for(int i = 0; i < mainAdapter.total(); i++){
                    List list = mainAdapter.explodeItem(i, null);
                    if(list != null && list.size() == 0){
                        continue;
                    }
                    if (list == null || list.size() < 2) {//非文件夹
                        locationBean = (LocationBean) list.get(0);
                        locationBean.setParentIndex(i);
                        locationBean.setChildIndex(-1);
                        locationBean.setTitle("");
                        locationBean.setName(getAppName(locationBean.getPackageName()));

//                        baos = new ByteArrayOutputStream();
//                        if(null == locationBean.getImgDrawable()){
//                            byte[] b = locationBean.getImgByte();
//                            drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
//                        }else {
//                            drawable = locationBean.getImgDrawable();
//                        }
//                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(bitmap);
//                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                        drawable.draw(canvas);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                        locationBean.setImgByte(baos.toByteArray());
//                        locationBean.setName(appInfo.getName());
//                        locationBean.setAddBtn(0);
//                        locationBean.setStatus(0);
//                        locationBean.setPriority(0);
//                        locationBean.setInstalled(1);
//                        locationBean.setCanuninstalled(1);
                        int num = db.isExistPackage(locationBean.getPackageName());
                        //Log.d("hqtest","dir package is: " + infos.get(i).getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + i);
                        if(num == 0){
                            db.insertLocation(locationBean);
                        }else {
                            //db.updateLocation(locationBean);
                            db.updateIndex(locationBean);
                        }
                    } else {//文件夹
                        for(int j = 0; j < list.size(); j++){
                            locationBean = (LocationBean) list.get(j);
                            if(null == locationBean){
                                continue;
                            }
                            locationBean.setParentIndex(i);
                            locationBean.setChildIndex(j);
                            locationBean.setName(getAppName(locationBean.getPackageName()));
//                            locationBean.setTitle(appInfo.getTitle());
//                            locationBean.setPackageName(appInfo.getPackageName());

//                            baos = new ByteArrayOutputStream();
//                            if(null == locationBean.getImgDrawable()){
//                                byte[] b = locationBean.getImgByte();
//                                drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
//                            }else {
//                                drawable = locationBean.getImgDrawable();
//                            }
//                            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//                            Canvas canvas = new Canvas(bitmap);
//                            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                            drawable.draw(canvas);
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                            locationBean.setImgByte(baos.toByteArray());
//                            locationBean.setName(appInfo.getName());
//                            locationBean.setAddBtn(0);
//                            locationBean.setStatus(0);
//                            locationBean.setPriority(0);
//                            locationBean.setInstalled(1);
//                            locationBean.setCanuninstalled(1);
                            int num = db.isExistPackage(locationBean.getPackageName());
                            //Log.d("hqtest","dir package is: " + infos.get(i).getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + i);
                            if(num == 0){
                                db.insertLocation(locationBean);
                            }else {
                                //db.updateLocation(locationBean);
                                db.updateIndex(locationBean);
                            }
                        }
                    }
                }
                isStoringData = false;
            }
//        }).start();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        List<String> installedPackages = new ArrayList<>();
        List<LocationBean> lists = null;
        LocationBean mLocationBean = null;
        A:for(int k = 0; k < data.size(); k++) {
            lists = data.get(k);
            if (lists == null) continue;//如果是 添加按钮，跳过
            for (int i = 0; i < lists.size(); i++) {
                mLocationBean = lists.get(i);
                if(mLocationBean != null && mLocationBean.getInstalled() == AppState.INSTALLED){
                    installedPackages.add(mLocationBean.getPackageName());
                }
            }
        }
        if(installedPackages.size() != 0){
            EventBus.getDefault().post(new InstalledAnimEndEvent(installedPackages));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("heqq","myAppFragment onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("heqq","myAppFragment onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("heqq","myAppFragment onDestroy");
        EventBus.getDefault().unregister(this);
        resetMainDeleteFlag(false);
        resetSubDeleteFlag(false);
    }
}