package com.chinatsp.apppanel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
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

import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.AppConfigs.Priorities;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.adapter.AddAppAdapter;
import com.chinatsp.apppanel.adapter.MyAppInfoAdapter;
import com.chinatsp.apppanel.adapter.OnItemClickCallback;
import com.chinatsp.apppanel.db.MyAppDB;
import com.chinatsp.apppanel.event.CancelDownloadEvent;
import com.chinatsp.apppanel.event.DownloadEvent;
import com.chinatsp.apppanel.event.FailDownloadEvent;
import com.chinatsp.apppanel.event.InstalledAnimEndEvent;
import com.chinatsp.apppanel.event.NotRemindEvent;
import com.chinatsp.apppanel.event.StartDownloadEvent;
import com.chinatsp.apppanel.event.UninstallCommandEvent;
import com.chinatsp.apppanel.event.UpdateEvent;
import com.chinatsp.apppanel.utils.Utils;
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
import launcher.base.utils.recent.RecentAppHelper;

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
    private boolean isNeedSort = false;//??????????????????
    private List<String> downloadPkgs = new ArrayList<>();
    private String versionCode = "";
    private PackageManager pm;
    private PackageInfo pi;
    private int subParentIndex = -1;//sub??????????????????
    private String fromPkg = "";
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
            fromPkg = getArguments().getString(Constant.FROM);
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
        if(db.countLocation() == 0){//??????????????????
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

        getUsedApps();//??????data?????????????????????
        addPushInstalledApp();//????????????push?????????????????????
        deleteUninstallApp();//????????????????????????????????????????????????
        checkDVR();//????????????DVR
        if(isNeedSort){//??????????????????????????????????????????
            sortWithPriority();
        }
        addDownloadApps();//?????????????????????apps
        //????????????????????????????????????????????????
        refreshAppName();
        //??????????????????
        refreshIcon();
        loadingTv.setVisibility(View.GONE);
        mMyAppInfoAdapter = new MyAppInfoAdapter(view.getContext(), data, new OnItemClickCallback() {
            @Override
            public void onItemClick() {
                boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                if(isSubShow){
                    appInfoClassifyView.hideSubContainer();
                }
            }
        });
        appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
//        appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {//????????????
                appInfoClassifyView.setSoftKeyBoardStatus(true);
            }

            @Override
            public void keyBoardHide(int height) {//????????????
                appInfoClassifyView.setSoftKeyBoardStatus(false);
            }
        });
        return view;
    }

    public void setFromPkg(String fromPkg){
        this.fromPkg = fromPkg;
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
        //???????????????????????????????????????
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
            if(info == null){//????????????????????????????????????
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
    *  ????????????????????????????????????????????????,?????????????????????????????????
     */
    private void deleteUninstallApp(){
        List<ResolveInfo> allApps = getApps();
        for (int i = 0; i < allApps.size(); i++){
            //???????????????????????????MainActivity??????????????????
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

        //????????????list?????????0???
        for(int k = 0; k < data.size(); k++){
            if(data.get(k).size() == 0){
                data.remove(k);
                k--;
            }
        }
    }

    //??????data?????????????????????,????????????????????????
    private void getUsedApps(){
        for (String packages:AppLists.blackListApps) {
            A:for (int i = 0; i < data.size(); i++){
                List<LocationBean> lists = data.get(i);
                lists.removeAll(Collections.singleton(null));//?????????null??????
                for(int k = 0; k < lists.size(); k++){
                    if(packages.equals(lists.get(k).getPackageName())){
                        lists.remove(k);
                        k--;
                        AsyncSchedule.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(db.isExistPackage(packages) != 0){
                                    db.deleteLocation(packages);
                                }
                            }
                        });
                        break A;
                    }
                }
            }
        }


        for (int i = 0; i < data.size(); i++){
            List<LocationBean> lists = data.get(i);
            lists.removeAll(Collections.singleton(null));//?????????null??????
            if(lists.size() == 0){
                data.remove(i);
                i--;
            }else {
                for(int k = 0; k < lists.size(); k++){
                    lists.get(k).setParentIndex(i);
                    lists.get(k).setChildIndex(k);
                }
            }
        }
    }

    /*
    *  ????????????push?????????????????????
     */
    private void addPushInstalledApp(){
        //??????data??????????????????
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
        //???????????????????????????
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
    *  ??????????????????dvr
     */
    private void checkDVR(){
        ICarService carService = (ICarService) AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        if(carService.isHasDVR()){
            //???DVR???addPushInstalledApp????????????
        }else {//???DVR
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
            //??????delete??????
            resetSubDeleteFlag(false);
            changeTitle((ChangeTitleEvent)event);
        }else if(event instanceof HideSubContainerEvent){
            //??????delete??????
            resetSubDeleteFlag(false);
            appInfoClassifyView.hideSubContainer();
        }else if(event instanceof AppInstallStatusEvent){
            //??????delete??????
            //resetDeleteFlag(false,-1);
            int status = ((AppInstallStatusEvent) event).getStatus();
            String packageName = ((AppInstallStatusEvent) event).getPackageName();
            Log.d(TAG,"status = " + status + ",pacakageName is: " + packageName);
            if(status == 1){//??????
                resetMainDeleteFlag(false);
                data = db.getData1();
                if(data.size() == 0){
                    getOriginalData();
                }
                //??????????????????
                refreshIcon();
                mMyAppInfoAdapter = new MyAppInfoAdapter(getContext(), data, new OnItemClickCallback() {
                    @Override
                    public void onItemClick() {
                        boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                        if(isSubShow){
                            appInfoClassifyView.hideSubContainer();
                        }
                    }
                });
                appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
//                appInfoClassifyView.setCanUninstallNameLists(getCanUninstallLists());
            }else {//??????
                if(!AppLists.isInBlackListApp(packageName)){
                    List<LocationBean> lists;
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    Log.d(TAG,"isSubShow = " + isSubShow);
                    A:for(int k = 0; k < data.size(); k++){
                        lists = data.get(k);
                        if(lists == null) continue;//????????? ?????????????????????
                        //lists.removeAll(Collections.singleton(null));//?????????null??????
                        for(int i = 0; i < lists.size(); i++) {
                            locationBean = lists.get(i);
                            if(locationBean == null) continue;
                            if (packageName.equals(locationBean.getPackageName())) {
                                if(lists.size() == 1){//?????????main
                                    data.remove(lists);
                                    if(isSubShow){
                                        appInfoClassifyView.hideSubContainer();
                                    }
                                }else if(lists.size() <= 3){//?????????sub?????????????????????????????????sub
                                    lists.remove(locationBean);
                                    lists.removeAll(Collections.singleton(null));//?????????null??????
                                    if(isSubShow){
                                        appInfoClassifyView.hideSubContainer();
                                    }
                                }else {
                                    lists.remove(locationBean);
                                }

                                //??????????????????????????????????????????????????????notifyDataSetChanged
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
        }else if(event instanceof UninstallCommandEvent) {//??????????????????????????????
            Log.d("CountTimer", "UninstallCommandEvent start count");
            appInfoClassifyView.startCountTimer();
        } else if(event instanceof StartDownloadEvent){//??????????????????
            Log.d("DownloadEvent","StartDownloadEvent");
            locationBean = ((StartDownloadEvent) event).getLocationBean();
            String pkgName = locationBean.getPackageName();

            if(data != null && data.size() != 0){
                List<LocationBean> lists;
                LocationBean mLocationBean;
                int k;
                //??????data????????????????????????
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
                    for(int i = 0; i < lists.size(); i++){
                        mLocationBean = lists.get(i);
                        if(mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())){
                            //do nothing
                            Log.d("DownloadEvent","StartDownloadEvent data already has " + pkgName);
                            break A;
                        }
                    }
                }

                //??????data??????????????????????????????
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
        }else if(event instanceof DownloadEvent){//????????????
            Log.d("DownloadEvent","DownloadEvent");
            locationBean = ((DownloadEvent) event).getLocationBean();
            int appState = locationBean.getInstalled();
            String pkgName = locationBean.getPackageName();
            if(data != null && data.size() != 0){
                //???????????????????????????
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
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
                //data??????????????????
                if(k < data.size()){
                    mMyAppInfoAdapter.notifyItemChanged(k);
                    boolean isSubShow = appInfoClassifyView.isSubContainerShow();
                    subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    //??????????????????????????????????????????sub
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
                        //???????????????download???????????????
                        db.deleteDownload(pkgName);
                    }
                }
            }
        }else if(event instanceof CancelDownloadEvent){//????????????
            String packageName = ((CancelDownloadEvent) event).getPackageName();
            if(data != null && data.size() != 0){
                //???????????????????????????
                List<LocationBean> lists;
                LocationBean mLocationBean;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && packageName.equals(mLocationBean.getPackageName())) {
                            //????????????????????????
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
        }else if(event instanceof UpdateEvent){//??????
            Log.d("DownloadEvent","UpdateEvent");
            locationBean = ((UpdateEvent) event).getLocationBean();
            String pkgName = locationBean.getPackageName();
            if(data != null && data.size() != 0){
                //???????????????????????????
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            mLocationBean.setInstalled(locationBean.getInstalled());
                            mLocationBean.setReserve3(locationBean.getReserve3());
                            break A;
                        }
                    }
                }

                //data??????????????????
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
                    //??????download???????????????
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof NotRemindEvent){//???????????? ??????????????????
            Log.d("DownloadEvent","NotRemindEvent");
            String reverse3 = ((NotRemindEvent) event).getReverse3();
            String pkgName = ((NotRemindEvent) event).getPackageName();
            if(data != null && data.size() != 0){
                //???????????????????????????
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            //?????????????????????????????? ???????????? ???????????????????????????
                            mLocationBean.setReserve2(reverse3);
                            break A;
                        }
                    }
                }

                //data??????????????????
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
                    //??????download???????????????
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof FailDownloadEvent){//????????????
            Log.d("DownloadEvent","FailDownloadEvent");
            String pkgName = ((FailDownloadEvent) event).getLocationBean().getPackageName();
            int installed =  ((FailDownloadEvent) event).getLocationBean().getInstalled();
            if(data != null && data.size() != 0){
                //???????????????????????????
                List<LocationBean> lists = null;
                LocationBean mLocationBean = null;
                int k;
                A:for(k = 0; k < data.size(); k++) {
                    lists = data.get(k);
                    if (lists == null) continue;//????????? ?????????????????????
                    for(int i = 0; i < lists.size(); i++) {
                        mLocationBean = lists.get(i);
                        if (mLocationBean != null && pkgName.equals(mLocationBean.getPackageName())) {
                            mLocationBean.setInstalled(installed);
                            break A;
                        }
                    }
                }

                //data??????????????????
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
                    //??????download???????????????
                    db.deleteDownload(pkgName);
                }
            }
        }else if(event instanceof InstalledAnimEndEvent){//?????????????????????
            Log.d("DownloadEvent","InstalledAnimEndEvent");
            List<String> installedPackages = ((InstalledAnimEndEvent) event).getInstalledPackages();
            if(installedPackages != null){
                if(data != null && data.size() != 0){
                    //???????????????????????????
                    List<LocationBean> lists = null;
                    LocationBean mLocationBean = null;
                    int k;
                    boolean isSubShow;
                    int num;
                    A:for(k = 0; k < data.size(); k++) {
                        lists = data.get(k);
                        if (lists == null) continue;//????????? ?????????????????????
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
//            Intent intent = new Intent();
//            intent.setClassName("com.chinatsp.launcher","com.chinatsp.launcher.CarLauncher");
//            getContext().startActivity(intent);
            jumpAction();
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

    /*
    *  ??????fromPkg??????????????????????????????fromPkg?????????????????????;
    *  ????????????????????????????????????????????????????????????HMSCore????????????????????????????????????com.huawei.hmsforcar.carappinit??????????????????
    *  ?????????????????????????????????????????????????????????????????????
     */
    private void jumpAction(){
        Log.d(TAG,"fromPkg: " + fromPkg);
        if(TextUtils.isEmpty(fromPkg) || AppLists.launcher.equals(fromPkg) || !Utils.isAppRunning(getContext(),fromPkg)){
            Intent intent = new Intent();
            intent.setClassName("com.chinatsp.launcher","com.chinatsp.launcher.CarLauncher");
            getContext().startActivity(intent);
        }else {
            RecentAppHelper.launchApp(getContext(),fromPkg);
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

        titleLists = titleLists.stream().distinct().collect(Collectors.toList());//???????????????
        if(titleLists.contains(title)){
            Pattern pattern = Pattern.compile("\\d+$");
            Matcher matcher = pattern.matcher(title);

            int num = 1;
            if(matcher.find()){
                String s = matcher.group();
                Log.d(TAG,"????????? " + title + " ?????????????????????????????????????????????" + s);
                title = title.replace(s,"");
                num = Integer.parseInt(s);
            } else{
                Log.d(TAG,"?????????" + title + "????????????????????????");
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
     * ????????????????????????APP
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
    *  ?????????????????????APP
     */
    private List<ResolveInfo> getAvailabelApps(List<ResolveInfo> allApps){
        for (String packages:AppLists.blackListApps) {
            A:for (int i = 0; i < allApps.size(); i++){
                if(packages.equals(allApps.get(i).activityInfo.packageName)){
                    allApps.remove(i);
                    //????????? ??????????????????????????????????????????aar??????aar???manifest???????????????Activity,?????????????????????2?????????
                    //break A;
                    i--;
                }
            }
        }
        return allApps;
    }

    /*
    *  ???????????????APP
     */
    private List<ResolveInfo> removeRepeatApps(List<ResolveInfo> allApps){
//        List<String> packageLists = new ArrayList<>();
//        for(int i = 0; i < allApps.size(); i++){
//            packageLists.add(allApps.get(i).activityInfo.packageName);
//        }
//        packageLists = packageLists.stream().distinct().collect(Collectors.toList());//?????????????????????
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
    * ??????????????????????????????????????????
     */
    private List<String> getInstalledPackages(List<ResolveInfo> list){
        List<String> packageLists = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            packageLists.add(list.get(i).activityInfo.packageName);
        }
        packageLists = packageLists.stream().distinct().collect(Collectors.toList());//?????????????????????
        return packageLists;
    }

    /*
    *  All app????????????
    *  ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    *  ??????????????????????????????
    */
    private void sortWithPriority(){
        Collections.sort(data, new Comparator<List<LocationBean>>() {
            @Override
            public int compare(List<LocationBean> o1, List<LocationBean> o2) {
                //0???????????????1???????????????-1????????????
                int i = o1.get(0).getPriority() - o2.get(0).getPriority();
                return i;
            }
        });

        //??????????????????????????????onResume()?????????
    }

    /*
    * ?????????????????????apps
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

            //??????data????????????????????????
            int k;
            A:for(k = 0; k < data.size(); k++) {
                dataLists = data.get(k);
                if (dataLists == null) continue;//????????? ?????????????????????
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
            //??????data??????????????????????????????
            if(k >= data.size()){
                locationBean.setParentIndex(data.size());
                locationBean.setChildIndex(-1);
                inner = new ArrayList<>();
                inner.add(locationBean);
                data.add(inner);
            }
            //onResume?????????????????????location???

            int isInstalled = locationBean.getInstalled();
            if(isInstalled == AppState.INSTALLED || isInstalled == AppState.INSTALLED_COMPLETELY){
                db.deleteDownload(pkgName);
            }
        }
    }

    /*
    * ????????????????????????????????????????????????
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
    * ??????????????????
     */
    private void refreshIcon(){
        String pkgName = "";
        List<ResolveInfo> allApps = getApps();
        for(List<LocationBean> lists:data){
            if(lists != null){
                for(int i = 0; i < lists.size(); i++){
                    locationBean = lists.get(i);
                    if(locationBean != null){
                        pkgName = locationBean.getPackageName();
                        drawable = AppLists.getResId(getContext(),pkgName);

                        if(drawable == null){
                            A:for(int k = 0; k < allApps.size(); k++){
                                if(pkgName.equals(allApps.get(k).activityInfo.packageName)){
                                    drawable = allApps.get(k).activityInfo.loadIcon(getContext().getPackageManager());
                                    break A;
                                }
                            }
                        }

                        if(drawable != null){
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
                //????????????????????????????????????????????????????????????
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
                    if (list == null || list.size() < 2) {//????????????
                        locationBean = (LocationBean) list.get(0);
                        locationBean.setParentIndex(i);
                        locationBean.setChildIndex(-1);
                        locationBean.setTitle("");
                        //locationBean.setName(getAppName(locationBean.getPackageName())); fix bug78084

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
                    } else {//?????????
                        for(int j = 0; j < list.size(); j++){
                            locationBean = (LocationBean) list.get(j);
                            if(null == locationBean){
                                continue;
                            }
                            locationBean.setParentIndex(i);
                            locationBean.setChildIndex(j);
                            //locationBean.setName(getAppName(locationBean.getPackageName())); fix bug78084
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
            if (lists == null) continue;//????????? ?????????????????????
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