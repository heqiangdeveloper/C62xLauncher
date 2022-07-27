package com.chinatsp.apppanel.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.anarchy.classifyview.event.AppInstallStatusEvent;
import com.anarchy.classifyview.event.Event;
import com.chinatsp.apppanel.AppConfigs.AppLists;
import com.chinatsp.apppanel.adapter.MyAppInfoAdapter;
import com.chinatsp.apppanel.bean.LocationBean;
import com.chinatsp.apppanel.db.MyAppDB;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import launcher.base.async.AsyncSchedule;

public class AppInstallStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInstallStatusReceiver";
    private MyAppDB db;
    private LocationBean locationBean;
    private int maxParentIndex = -1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
            Log.d(TAG,"onReceive ACTION_PACKAGE_ADDED");
            //安装成功
            String packageName = intent.getData().getSchemeSpecificPart();
            if(!AppLists.isInBlackListApp(packageName)){
                db = new MyAppDB(context);
                if(db.isExistPackage(packageName) == 0){//如果数据库中没有记录
                    locationBean = new LocationBean();
                    maxParentIndex = db.getMaxParentIndex();
                    A:for(ResolveInfo info : getApps(context)){
                        if(packageName.equals(info.activityInfo.packageName)){
                            locationBean.setParentIndex(++maxParentIndex);
                            locationBean.setChildIndex(-1);
                            locationBean.setPackageName(info.activityInfo.packageName);
                            locationBean.setName((info.activityInfo.loadLabel(context.getPackageManager())).toString());
                            Drawable drawable = info.activityInfo.loadIcon(context.getPackageManager());

                            locationBean.setImgDrawable(drawable);
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//                            Canvas canvas = new Canvas(bitmap);
//                            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                            drawable.draw(canvas);
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                            locationBean.setImgByte(baos.toByteArray());

                            locationBean.setTitle("");
                            locationBean.setAddBtn(0);
                            locationBean.setStatus(0);
                            locationBean.setPriority(0);
                            locationBean.setInstalled(1);
                            locationBean.setCanuninstalled(AppLists.isSystemApplication(context,info.activityInfo.packageName) ? 0:1);
                            break A;
                        }
                    }

                    //写入数据库中
                    AsyncSchedule.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.insertLocation(locationBean);
                            EventBus.getDefault().post(new AppInstallStatusEvent(1,packageName));
                        }
                    });
                }else {
                    Log.d(TAG,"already installed： " + packageName);
                }
            }
        }else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
            //卸载成功
            String packageName = intent.getData().getSchemeSpecificPart();
            EventBus.getDefault().post(new AppInstallStatusEvent(0,packageName));
        }
    }

    /**
     * 获取系统中所有的APP
     * @return
     */
    private List<ResolveInfo> getApps(Context context){
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        return packageManager.queryIntentActivities(i,0);
    }
}
