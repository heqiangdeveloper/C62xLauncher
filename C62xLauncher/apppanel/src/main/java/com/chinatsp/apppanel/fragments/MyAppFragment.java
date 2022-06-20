package com.chinatsp.apppanel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
        Log.d("hqtest","db.countLocation() = " + db.countLocation());
        if(db.countLocation() == 0){//没有数据记录
            List<ResolveInfo> allApps = getApps();
            allApps = getAvailabelApps(allApps);
            for(ResolveInfo info : allApps){
                //L.d("name: " + info.activityInfo.loadLabel(getContext().getPackageManager()) + "," + info.activityInfo.packageName);
                List<LocationBean> inner = new ArrayList<>();
                locationBean = new LocationBean();
                locationBean.setPackageName(info.activityInfo.packageName);
                drawable = info.activityInfo.loadIcon(getContext().getPackageManager());

                locationBean.setImgByte(null);
                locationBean.setImgDrawable(drawable);
                locationBean.setName((info.activityInfo.loadLabel(getContext().getPackageManager())).toString());
                locationBean.setTitle("");
                locationBean.setCanuninstalled(AppLists.packageUninstallStatus(info.activityInfo.packageName));
                inner.add(locationBean);
                data.add(inner);
            }
        }else {
            data = db.getData1();
        }

        loadingTv.setVisibility(View.GONE);
        mMyAppInfoAdapter = new MyAppInfoAdapter(view.getContext(), data);
        appInfoClassifyView.setAdapter(mMyAppInfoAdapter);
        return view;
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
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.deleteLocation(packageName);
                                }
                            }).start();
                            break A;
                        }
                    }
                }
            }
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("heqq","myAppFragment onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("heqq","myAppFragment onStop");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //先清空数据库，再保存桌面布局数据到数据库
//                if(db.countLocation() != 0){
//                    db.deleteLocation();
//                }
                editor.putBoolean(MyConfigs.SHOWDELETE,false);
                editor.putInt(MyConfigs.SHOWDELETEPOSITION,-1);
                editor.commit();
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

                        baos = new ByteArrayOutputStream();
                        if(null == locationBean.getImgDrawable()){
                            byte[] b = locationBean.getImgByte();
                            drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
                        }else {
                            drawable = locationBean.getImgDrawable();
                        }
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        locationBean.setImgByte(baos.toByteArray());
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
                            db.updateLocation(locationBean);
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

                            baos = new ByteArrayOutputStream();
                            if(null == locationBean.getImgDrawable()){
                                byte[] b = locationBean.getImgByte();
                                drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
                            }else {
                                drawable = locationBean.getImgDrawable();
                            }
                            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            drawable.draw(canvas);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            locationBean.setImgByte(baos.toByteArray());
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
                                db.updateLocation(locationBean);
                            }
                        }
                    }
                }
            }
        }).start();
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
        EventBus.getDefault().unregister(this);
        Log.d("heqq","myAppFragment onDestroy");
    }
}