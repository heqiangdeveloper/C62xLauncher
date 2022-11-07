package com.chinatsp.apppanel.window;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.adapter.AppManageAdapter;
import com.chinatsp.apppanel.decoration.AppManageDecoration;
import com.chinatsp.apppanel.event.DeletedCallback;
import com.chinatsp.apppanel.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import launcher.base.async.AsyncSchedule;
import launcher.base.service.AppServiceManager;
import launcher.base.service.tencentsdk.ITencentSdkService;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.C62Toast;

public class AppManagementWindow {
    private static final String TAG = "AppManagementWindow";
    private static AppManagementWindow appManagementWindow;
    private WindowManager winManager;
    private Context mContext;
    private View view;
    private boolean isShow = false;
    private final int MAX_RECENT_APPS = 20;
    private List<HashMap<String,Object>> appInfos = new ArrayList<HashMap<String,Object>>();
    AppManageAdapter appManageAdapter;
    public AppManagementWindow(Context context) {
        mContext = context;
        winManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static synchronized AppManagementWindow getInstance(Context context) {
        if (appManagementWindow == null) {
            appManagementWindow = new AppManagementWindow(context);
        }
        return appManagementWindow;
    }

    private void checkPermission() {
        if (!Settings.canDrawOverlays(mContext)) {
            //没有悬浮窗权限,跳转申请
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            mContext.startActivity(intent);
            Log.i(TAG, "checkPermission permission");
        } else {
            Log.i(TAG, "checkPermission granted!");
        }
    }

    public void show(){
        if(isShow) return;
        isShow = true;
        checkPermission();
        WindowManager.LayoutParams params = getParams();
        view = LayoutInflater.from(mContext).inflate(R.layout.appmanage_dialog_layout, null);
        //区域外点击消失，目前因为背景是1920*720，不支持
//        view.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch");
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//                Rect rect = new Rect();
//                view.getGlobalVisibleRect(rect);
//                if (!rect.contains(x, y)) {
//                    hide();
//                }
//
//                Log.d(TAG, "onTouch : " + x + ", " + y + ", rect: "
//                        + rect);
//                return false;
//            }
//
//        });

        TextView clearTv = (TextView) view.findViewById(R.id.clear_tv);
        ImageView closeIv = (ImageView) view.findViewById(R.id.close_iv);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.appmanage_recyclerview);
        TextView warnTv = (TextView) view.findViewById(R.id.warn_tv);

        appInfos = RecentAppHelper.getRecentApps(mContext,MAX_RECENT_APPS,RecentAppHelper.FROM_APPMANAGEMENT);
        if(appInfos.size() == 0){
            warnTv.setVisibility(View.VISIBLE);
            clearTv.setVisibility(View.GONE);
        }else {
            warnTv.setVisibility(View.GONE);
            clearTv.setVisibility(View.VISIBLE);
        }
        appManageAdapter = new AppManageAdapter(mContext, appInfos, new DeletedCallback() {
            @Override
            public void onDeleted() {
                hide();
            }
        });
        rv.setAdapter(appManageAdapter);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false));
        rv.addItemDecoration(new AppManageDecoration(100,0));
        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                AsyncSchedule.execute(new Runnable() {
                    @Override
                    public void run() {
                        getMemorySize();
                    }
                });
            }
        });
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        Log.i(TAG, "show appMangementWindow");
        winManager.addView(view, params);
    }

    public void hide(){
        if (null != winManager) {
            winManager.removeView(view);
            isShow = false;
        }
    }

    public boolean isShow(){
        return isShow;
    }

    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT > 25) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.CENTER;
        params.width = 1920; // pushView宽
        params.height = 720; // pushView高
        return params;
    }

    private void getMemorySize() {
        long memSize = 0L;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        List<HashMap<String,Object>> infos = appManageAdapter.getInfos();
        List<String> pkgLists = new ArrayList<>();
        for(int i = 0; i < infos.size(); i++){
            pkgLists.add((String)infos.get(i).get("packageName"));
        }

        for(int i = 0; i < appProcessList.size(); i++){
            if(appProcessList.get(i) != null &&
                    !pkgLists.contains(appProcessList.get(i).processName)){
                appProcessList.remove(i);
                i--;
            }
        }

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            // 获得该进程占用的内存
            int[] myMempid = new int[]{pid};
            // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
            // 获取进程占内存用信息 kb单位
            memSize += memoryInfo[0].dalvikPrivateDirty;

            Log.i("hqinfo", "processName: " + processName + "  pid: " + pid
                    + " uid:" + uid + " memorySize is -->" + memSize + "kb");
        }

        long finalMemSize = memSize;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(mContext, "已释放 " + Utils.byte2Format(finalMemSize * 1024) + "内存", Toast.LENGTH_SHORT).show();
                C62Toast.show(mContext, mContext.getString(R.string.appmanagement_clear_toast,Utils.byte2Format(finalMemSize * 1024)),3000);
            }
        });

        for(int i = 0; i < infos.size(); i++){
            String pkgName = (String) infos.get(i).get("packageName");
            if("com.tencent.wecarflow".equals(pkgName)){
                closeWecarFlowUI();
            }else {
                Utils.forceStopPackage(mContext,pkgName);
            }
        }
    }

    /*
    *  退出爱趣听UI，但不改变播放状态
     */
    private void closeWecarFlowUI() {
        ITencentSdkService service =
                (ITencentSdkService) AppServiceManager.getService(AppServiceManager.SERVICE_TENCENT_SDK);
        service.closeUI();
    }
}
