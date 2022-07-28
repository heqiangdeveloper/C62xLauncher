package com.chinatsp.apppanel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anarchy.classifyview.ClassifyView;
import com.anarchy.classifyview.adapter.MainRecyclerViewCallBack;
import com.anarchy.classifyview.event.AppInstallStatusEvent;
import com.anarchy.classifyview.event.ChangeTitleEvent;
import com.anarchy.classifyview.event.Event;
import com.anarchy.classifyview.event.HideSubContainerEvent;
import com.anarchy.classifyview.event.ReStoreDataEvent;
import com.anarchy.classifyview.listener.SoftKeyBoardListener;
import com.anarchy.classifyview.util.MyConfigs;
import com.chinatsp.apppanel.AppConfigs.AppLists;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.adapter.AddAppAdapter;
import com.chinatsp.apppanel.adapter.MyAppInfoAdapter;
import com.chinatsp.apppanel.bean.LocationBean;
import com.chinatsp.apppanel.db.MyAppDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import launcher.base.async.AsyncSchedule;

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
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private MyAppInfoAdapter mMyAppInfoAdapter;
    private List<List<LocationBean>> data;
    private static boolean isStoringData = false;
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
            getOriginalData();
        }else {
            data = db.getData1();
            Log.d(TAG,"data.size = " + data.size());
            if(data.size() == 0){
                getOriginalData();
            }
        }

        addPushInstalledApp();//添加通过push方式安装的应用
        deleteUninstallApp();//删除掉未安装的应用，应用管理除外
        loadingTv.setVisibility(View.GONE);
        mMyAppInfoAdapter = new MyAppInfoAdapter(view.getContext(), data);
        appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
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
                drawable = getResources().getDrawable(R.mipmap.ic_appmanagement);
                locationBean.setName(getResources().getString(R.string.appmanagement_name));
            }else {
                locationBean.setPackageName(info.activityInfo.packageName);
                drawable = info.activityInfo.loadIcon(getContext().getPackageManager());
                locationBean.setName((info.activityInfo.loadLabel(getContext().getPackageManager())).toString());
            }
            locationBean.setCanuninstalled(AppLists.isSystemApplication(getContext(),locationBean.getPackageName()) ? 0:1);
            locationBean.setTitle("");
            locationBean.setImgByte(null);
            locationBean.setImgDrawable(drawable);
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
    *  删除掉未安装的应用，应用管理除外
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
                if(!pkgName.equals(AppLists.APPMANAGEMENT) && !packageLists.contains(pkgName)){
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
                locationBean.setImgDrawable(allApps.get(i).activityInfo.loadIcon(getContext().getPackageManager()));
                locationBean.setName(allApps.get(i).activityInfo.loadLabel(getContext().getPackageManager()).toString());

                locationBean.setCanuninstalled(AppLists.isSystemApplication(getContext(),locationBean.getPackageName()) ? 0:1);
                locationBean.setTitle("");
                locationBean.setImgByte(null);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event){
        if(event instanceof ChangeTitleEvent){
            mMyAppInfoAdapter.changeTitle((ChangeTitleEvent)event);
        }else if(event instanceof HideSubContainerEvent){
            appInfoClassifyView.hideSubContainer();
        }else if(event instanceof AppInstallStatusEvent){
            int status = ((AppInstallStatusEvent) event).getStatus();
            String packageName = ((AppInstallStatusEvent) event).getPackageName();
            Log.d(TAG,"status = " + status + ",pacakageName is: " + packageName);
            if(status == 1){//安装
                data = db.getData1();
                if(data.size() == 0){
                    getOriginalData();
                }
                mMyAppInfoAdapter = new MyAppInfoAdapter(getContext(), data);
                appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
            }else {//卸载
                if(!AppLists.isInBlackListApp(packageName)){
                    A:for(List<LocationBean> lists:data){
                        if(lists != null && lists.size() < 2 && lists.get(0) != null &&
                                lists.get(0).getPackageName().equals(packageName)){
                            data.remove(lists);
                            mMyAppInfoAdapter = new MyAppInfoAdapter(getContext(), data);
                            appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
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
        }else if(event instanceof ReStoreDataEvent){
            if(!isStoringData) storeData();
        }
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

    /*
    *  剔除黑名单中的APP
     */
    private List<ResolveInfo> getAvailabelApps(List<ResolveInfo> allApps){
        for (String packages:AppLists.blackListApps) {
            A:for (int i = 0; i < allApps.size(); i++){
                if(packages.equals(allApps.get(i).activityInfo.packageName)){
                    allApps.remove(i);
                    break A;
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
        editor.putBoolean(MyConfigs.SHOWDELETE,false);
        editor.putInt(MyConfigs.SHOWDELETEPOSITION,-1);
        editor.commit();
    }
}