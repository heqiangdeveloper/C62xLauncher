package com.chinatsp.apppanel.adapter;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.event.ChangeSubTitleEvent;
import com.anarchy.classifyview.event.HideSubContainerEvent;
import com.anarchy.classifyview.simple.SimpleAdapter;
import com.anarchy.classifyview.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.util.MyConfigs;
import launcher.base.applists.AppLists;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chinatsp.apppanel.AppConfigs.Constant;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.bean.InfoBean;
import com.chinatsp.apppanel.db.MyAppDB;
import com.chinatsp.apppanel.event.CancelDownloadEvent;
import com.chinatsp.apppanel.event.InstalledAnimEndEvent;
import com.chinatsp.apppanel.event.NotRemindEvent;
import com.chinatsp.apppanel.event.SelectedCallback;
import com.chinatsp.apppanel.event.UninstallCommandEvent;
import com.chinatsp.apppanel.service.AppStoreService;
import com.chinatsp.apppanel.time.CalcuTimer;
import com.chinatsp.apppanel.time.CalcuTimerCallback;
import com.chinatsp.apppanel.utils.Utils;
import com.chinatsp.apppanel.window.AppManagementWindow;
import com.huawei.appmarket.launcheragent.CommandType;
import com.huawei.appmarket.launcheragent.launcher.AppState;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import launcher.base.utils.glide.GlideCommonRequestOption;
import launcher.base.utils.glide.RoundBitmapTransformation;
import launcher.base.utils.glide.RoundCornerImage;
import launcher.base.utils.property.PropertyUtils;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.CircleProgressView;
import launcher.base.utils.view.RoundImageView;

public class MyAppInfoAdapter extends SimpleAdapter<LocationBean, MyAppInfoAdapter.ViewHolder> {
    public List<List<LocationBean>> mData;
    public Context context;
    private MyAppDB db;
    private LocationBean locationBean;
    private InfoBean infoBean;
    //private ByteArrayOutputStream baos;
    //private Bitmap bitmap;
    //private Drawable drawable;
    private List<String> titleLists = new ArrayList<>();
    private List<LocationBean> infos = null;
    private String titleStr = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int showdeleteposition;
    private String title;
    private boolean showDelete;
    private int selectSize = 0;
    private List<HashMap<String,Object>> appInfos = new ArrayList<HashMap<String,Object>>();
    private final int MAX_RECENT_APPS = 20;
    private static boolean isClickDelete = false;
    private static final String TAG = MyAppInfoAdapter.class.getName();
    private int subParentIndex = -1;//sub??????????????????
    private CalcuTimer timer;
    private OnItemClickCallback onItemClickCallback;

    public MyAppInfoAdapter(Context context, List<List<LocationBean>> mData,OnItemClickCallback onItemClickCallback) {
        super(mData);
        this.mData = mData;
        this.onItemClickCallback = onItemClickCallback;
        mData.removeAll(Collections.singleton(null));//?????????null??????
        this.context = context;
        preferences = context.getSharedPreferences(MyConfigs.APPPANELSP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        db = new MyAppDB(context);
        for(int i = 0; i < mData.size(); i++){
            infos = mData.get(i);
            infos.removeAll(Collections.singleton(null));
            if(infos.size() == 0){
                mData.remove(i);
                i--;
            }else {
                titleLists.add(infos.get(0).getTitle());
            }
        }
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        super.onBindMainViewHolder(holder, position);
        List<LocationBean> infos = mData.get(position);
        //holder.tvName.setText("");
        String title = holder.tvName.getText().toString();
        Log.d("hqtest","info size is: " + infos.size());
        if(infos != null && infos.size() > 1){//?????????
            if(!TextUtils.isEmpty(infos.get(0).getTitle())){
                titleStr = infos.get(0).getTitle();
            }else {
                int index = getExistDirIndex(db.getAllTitles());
                if(index == -1){
                    titleStr = "?????????";
                }else {
                    index++;
                    titleStr = "?????????" + index;
                }
            }
            for(LocationBean locationBean : infos){
                if(locationBean != null){
                    locationBean.setTitle(titleStr);
                }
            }
            //??????????????????  ????????????????????????>?????????>?????????>?????????
            List<Integer> installedLists = new ArrayList<>();
            int installed;
            for(LocationBean locationBean : infos){
                if(locationBean != null){
                    installed = locationBean.getInstalled();
                    installedLists.add(installed);
                }
            }

            if(installedLists.contains(AppState.DOWNLOAD_FAIL)){//????????????
                setDownloadStatus(true,holder,AppState.DOWNLOAD_FAIL,0,null);
                holder.tvName.setText(getName(AppState.DOWNLOAD_FAIL,titleStr));
                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                if(position == subParentIndex){
                    EventBus.getDefault().post(new ChangeSubTitleEvent(holder.tvName.getText().toString(),position));
                }
            }else if(installedLists.contains(AppState.DOWNLOADING)){//?????????
                //????????????????????????
                int total = 0;
                int num = 0;
                for(LocationBean locationBean : infos){
                    if(locationBean != null){
                        installed = locationBean.getInstalled();
                        if(installed == AppState.DOWNLOADING){
                            total += locationBean.getStatus();
                            num++;
                        }
                    }
                }
                if(num != 0) total = total / num;
                setDownloadStatus(true,holder,AppState.DOWNLOADING,total,null);
                holder.tvName.setText(getName(AppState.DOWNLOADING,titleStr));
                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                if(position == subParentIndex){
                    EventBus.getDefault().post(new ChangeSubTitleEvent(holder.tvName.getText().toString(),position));
                }
            }else if(installedLists.contains(AppState.DOWNLOAD_PAUSED)){//?????????
                //????????????????????????
                int total = 0;
                int num = 0;
                for(LocationBean locationBean : infos){
                    if(locationBean != null){
                        installed = locationBean.getInstalled();
                        if(installed == AppState.DOWNLOAD_PAUSED){
                            total += locationBean.getStatus();
                            num++;
                        }
                    }
                }
                if(num != 0) total = total / num;
                setDownloadStatus(true,holder,AppState.DOWNLOAD_PAUSED,total,null);
                holder.tvName.setText(getName(AppState.DOWNLOAD_PAUSED,titleStr));
                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                if(position == subParentIndex){
                    EventBus.getDefault().post(new ChangeSubTitleEvent(holder.tvName.getText().toString(),position));
                }
            }else if(installedLists.contains(AppState.INSTALLING)){//?????????
                setDownloadStatus(true,holder,AppState.INSTALLING,0,null);
                holder.tvName.setText(getName(AppState.INSTALLING,titleStr));
                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                if(position == subParentIndex){
                    EventBus.getDefault().post(new ChangeSubTitleEvent(holder.tvName.getText().toString(),position));
                }
            }else if(installedLists.contains(AppState.INSTALLED)){//????????????
                List<String> installedPackages = new ArrayList<>();
                int mInstalled;
                for(LocationBean locationBean : infos){
                    if(locationBean != null){
                        mInstalled = locationBean.getInstalled();
                        if(mInstalled == AppState.INSTALLED){
                            installedPackages.add(locationBean.getPackageName());
                        }
                    }
                }

                setDownloadStatus(true,holder,AppState.INSTALLED,0,installedPackages);
                holder.tvName.setText(getName(AppState.INSTALLED,titleStr));
                subParentIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                if(position == subParentIndex){
                    //??????sub???????????????????????????????????????
                    EventBus.getDefault().post(new ChangeSubTitleEvent(holder.tvName.getText().toString(),position));
                }
            }else {//??????????????????
                setDownloadStatus(true,holder,AppState.INSTALLED_COMPLETELY,0,null);
                holder.tvName.setText(getName(AppState.INSTALLED_COMPLETELY,titleStr));
            }

//            holder.tvName.setText(titleStr);
            holder.deleteIv.setVisibility(View.GONE);

            for(int i = 0; i < infos.size(); i++){
                if(infos.get(i) == null){
                    continue;
                }
                LocationBean lb = infos.get(i);
                holder.deleteIv.setTag(lb.getCanuninstalled());
                //????????????position???????????????MyAppFragment getOriginalData??????index
//                locationBean.setParentIndex(position);
//                locationBean.setChildIndex(i);
                  //infos.get(i).setTitle(titleStr);
//                locationBean.setPackageName(infos.get(i).getPackageName());
//
//                Drawable drawable;
//                if(null == lb.getImgDrawable()){
//                    byte[] b = lb.getImgByte();
//                    drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
//                }else {
//                    drawable = lb.getImgDrawable();
//                }
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                drawable.draw(canvas);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                lb.setImgByte(baos.toByteArray());
//                locationBean.setName(infos.get(i).getName());
//                locationBean.setAddBtn(0);
//                locationBean.setStatus(0);
//                locationBean.setPriority(0);
//                locationBean.setInstalled(1);
//                locationBean.setCanuninstalled(1);
                int num = db.isExistPackage(lb.getPackageName());
                Log.d("hqtest","dir package is: " + lb.getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + i);
                if(num == 0){
                    db.insertLocation(lb);
                }else {
                    db.updateTitle(lb);
                }
            }
        } else if(infos.size() == 1){
            locationBean = mData.get(position).get(0);
            holder.deleteIv.setTag(locationBean.getCanuninstalled());
            //????????????????????????
            showDelete = preferences.getBoolean(MyConfigs.MAINSHOWDELETE,false);
            //????????? ????????????????????????????????????
            //if(parentIndex != -1 && parentIndex == position){
            if(showDelete){
                holder.deleteIv.setVisibility(locationBean.getCanuninstalled() == 1 ? View.VISIBLE : View.GONE);
            }else {
                holder.deleteIv.setVisibility(View.GONE);
            }

            //??????????????????
            List<String> installedPackages = new ArrayList<>();
            installedPackages.add(locationBean.getPackageName());
            setDownloadStatus(false,holder,locationBean.getInstalled(),locationBean.getStatus(), installedPackages);
            //??????????????????
            holder.tvName.setText(getName(locationBean.getInstalled(),locationBean.getName()));

            //????????????position???????????????MyAppFragment getOriginalData??????index
//            locationBean.setParentIndex(position);
//            locationBean.setChildIndex(-1);
            locationBean.setTitle("");
//            locationBean.setPackageName(mData.get(position).get(0).getPackageName());
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            Drawable drawable;
//            if(null == locationBean.getImgDrawable()){
//                byte[] b = locationBean.getImgByte();
//                drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
//            }else {
//                drawable = mData.get(position).get(0).getImgDrawable();
//            }
//            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            drawable.draw(canvas);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            locationBean.setImgByte(baos.toByteArray());
//            locationBean.setName(mData.get(position).get(0).getName());
//            locationBean.setAddBtn(0);
//            locationBean.setStatus(0);
//            locationBean.setPriority(0);
//            locationBean.setInstalled(1);
//            locationBean.setCanuninstalled(1);
            //????????????
            int num = db.isExistPackage(locationBean.getPackageName());
            Log.d("hqtest","package package is: " + locationBean.getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + -1);
            if(num == 0){
                db.insertLocation(locationBean);
            }else {
                db.updateTitle(locationBean);
            }

//            showDelete = preferences.getBoolean(MyConfigs.SHOWDELETE ,  false);
//            if(showDelete){
//                holder.deleteIv.setVisibility(mData.get(position).get(0).getCanuninstalled() == 1 ? View.VISIBLE : View.GONE);
//            }else {
//                holder.deleteIv.setVisibility(View.GONE);
//            }

        }
    }

    /*
    *  ??????????????????
     */
    private String getName(int installed,String name){
        if(installed == 0 || installed == AppState.INSTALLED || installed == AppState.COULD_UPDATE ||
            installed == AppState.INSTALLED_COMPLETELY){//?????????
            return getNewName(name);
        }else if(installed == AppState.DOWNLOADING){//?????????
            return context.getString(R.string.download_downloading);
        }else if(installed == AppState.DOWNLOAD_PAUSED){//?????????
            return context.getString(R.string.download_pause);
        }else if(installed == AppState.COULD_BE_CANCELED){//?????????
            return getNewName(name);
        }else if(installed == AppState.DOWNLOAD_FAIL){//????????????
            return context.getString(R.string.download_fail);
        }else if(installed == AppState.INSTALLING){//?????????
            return context.getString(R.string.download_installing);
        }else {
            return getNewName(name);
        }
    }

    private static final String FOLDERCN = "?????????";
    private static final String FOLDEREN = "Folder";
    private String getNewName(String name){
        String s = "";
        if(name.equals(FOLDERCN) || name.equals(FOLDEREN)){
            s = context.getString(R.string.folder_name);
        }else if(name.startsWith(FOLDERCN)){
            s = name.substring(FOLDERCN.length());
            if(Utils.isInteger(s)){
                s = name.replace(FOLDERCN,context.getString(R.string.folder_name));
            }else {
                s = name;
            }
        }else if(name.startsWith(FOLDEREN)){
            s = name.substring(FOLDEREN.length());
            if(Utils.isInteger(s)){
                s = name.replace(FOLDEREN,context.getString(R.string.folder_name));
            }else {
                s = name;
            }
        }else {
            s = name;
        }
        return s;
    }

    /*
    *   -1????????????????????? 0?????????????????? X????????????X???
     */
    private int getExistDirIndex(List<String> titleLists){
        int lastNumber = -1;
        for(String title : titleLists){
            if(title.startsWith("?????????") && title.length() <= 5){
                if(title.length() == 5){
                    if(title.substring(3,4).matches("[1-9]") && title.substring(4,5).matches("[0-9]")){
                        if(Integer.parseInt(title.substring(3,5)) > lastNumber){
                            lastNumber = Integer.parseInt(title.substring(3,5));
                        }
                    }
                }else if(title.length() == 4){
                    if(title.substring(3,4).matches("[0-9]")){
                        if(Integer.parseInt(title.substring(3,4)) > lastNumber){
                            lastNumber = Integer.parseInt(title.substring(3,4));
                        }
                    }
                }else {
                    if(lastNumber == -1) lastNumber = 0;
                }
            }
        }

        return lastNumber;
    }

    /*
    * @param isDir ????????????????????????????????????
    * @param installed ????????????
    * @param status ????????????
    * @param packags ???????????????????????????
     */
    private void setDownloadStatus(boolean isDir,ViewHolder holder,int installed,int status,List<String> packages){
        if(installed == 0 || installed == AppState.INSTALLED_COMPLETELY || installed == AppState.COULD_UPDATE){//???????????????
            holder.insertAbleGridView.setAlpha(1.0f);
            holder.tvName.setAlpha(1.0f);
            holder.loadStatusIv.setVisibility(View.GONE);
            holder.loadStatusIv.setImageDrawable(null);
            holder.loadStatusCircleProgressView.setVisibility(View.GONE);
        }else if(installed == AppState.INSTALLED){//?????????
            Drawable drawable = holder.loadStatusIv.getDrawable();
            if(drawable != null && (drawable instanceof AnimationDrawable)){
                AnimationDrawable AD = (AnimationDrawable) drawable;
                AD.stop();//????????????????????????
            }
//            String name = holder.tvName.getText().toString();
//            boolean isInstalling = false;
//            if((context.getString(R.string.download_installing)).equals(name)){
//                isInstalling = true;
//            }
//            boolean isLoadStatusIvVisible = holder.loadStatusIv.getVisibility() == View.VISIBLE ? true : false;
//            if(isInstalling && drawable != null && isLoadStatusIvVisible && (drawable instanceof AnimationDrawable)){
//                Log.d("Animation","name = " + name);
                //???????????????????????????????????????
                holder.insertAbleGridView.setAlpha(1.0f);
                holder.tvName.setAlpha(1.0f);
                holder.loadStatusCircleProgressView.setVisibility(View.GONE);
                holder.loadStatusIv.setVisibility(View.VISIBLE);
                holder.loadStatusIv.setImageResource(R.drawable.sweep_anim);
                AnimationDrawable AD = (AnimationDrawable) holder.loadStatusIv.getDrawable();
                AD.setOneShot(false);
                AD.start();

                Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                        Animation.RELATIVE_TO_SELF,0,
                        Animation.RELATIVE_TO_SELF,0,
                        Animation.RELATIVE_TO_SELF,0);
                trans.setDuration(5000);
                trans.setRepeatCount((10 * 1000) / 5000);//??????10s
                holder.loadStatusIv.startAnimation(trans);
                trans.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if(isDir){
                            Log.d("SweepAnimation","dir onAnimationStart");
                        }else {
                            if(packages != null) {
                                for(String pkg : packages){
                                    Log.d("SweepAnimation",pkg + ",onAnimationStart");
                                }
                            }
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AD.stop();
                        holder.insertAbleGridView.setAlpha(1.0f);
                        holder.tvName.setAlpha(1.0f);
                        holder.loadStatusIv.setVisibility(View.GONE);
                        holder.loadStatusIv.setImageBitmap(null);
                        holder.loadStatusCircleProgressView.setVisibility(View.GONE);

                        if(isDir){
                            Log.d("SweepAnimation","dir onAnimationEnd");
                        }else {
                            if(packages != null) {
                                //????????????????????????AppState.INSTALLED_COMPLETELY
                                EventBus.getDefault().post(new InstalledAnimEndEvent(packages));
                                for(String pkg : packages){
                                    Log.d("SweepAnimation",pkg + ",onAnimationEnd");
                                }
                            }
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
        }else if(installed == AppState.DOWNLOADING){//?????????
            holder.insertAbleGridView.setAlpha(0.5f);
            holder.tvName.setAlpha(0.5f);
            holder.loadStatusIv.setVisibility(View.GONE);
            holder.loadStatusIv.setImageDrawable(null);
            holder.loadStatusCircleProgressView.setMax(Constant.MAXVALUE);
            holder.loadStatusCircleProgressView.setCurrent(status);
            holder.loadStatusCircleProgressView.setTextVisible(Constant.PAUSE_LABEL,false);
            holder.loadStatusCircleProgressView.setVisibility(View.VISIBLE);
        }else if(installed == AppState.DOWNLOAD_PAUSED){//????????????
            holder.insertAbleGridView.setAlpha(0.5f);
            holder.tvName.setAlpha(0.5f);
            holder.loadStatusIv.setVisibility(View.GONE);
            holder.loadStatusIv.setImageDrawable(null);
            holder.loadStatusCircleProgressView.setMax(Constant.MAXVALUE);
            holder.loadStatusCircleProgressView.setCurrent(status);
            holder.loadStatusCircleProgressView.setTextVisible(Constant.PAUSE_LABEL,true);
            holder.loadStatusCircleProgressView.setVisibility(View.VISIBLE);
        }else if(installed == AppState.DOWNLOAD_FAIL){//????????????
            holder.insertAbleGridView.setAlpha(0.5f);
            holder.tvName.setAlpha(0.5f);
            holder.loadStatusCircleProgressView.setVisibility(View.GONE);
            holder.loadStatusIv.setImageResource(R.mipmap.apk_fail_load);
            holder.loadStatusIv.setVisibility(View.VISIBLE);
        }else if(installed == AppState.INSTALLING){//?????????
            holder.insertAbleGridView.setAlpha(0.5f);
            holder.tvName.setAlpha(0.5f);
            holder.loadStatusCircleProgressView.setVisibility(View.GONE);
            holder.loadStatusIv.setVisibility(View.VISIBLE);
            holder.loadStatusIv.setImageResource(R.drawable.installing_anim);
            AnimationDrawable AD = (AnimationDrawable) holder.loadStatusIv.getDrawable();
            AD.setOneShot(false);
            AD.start();

            Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0,
                    Animation.RELATIVE_TO_SELF,0);
            trans.setDuration(1000);
            trans.setRepeatCount(Animation.INFINITE);
            holder.loadStatusIv.startAnimation(trans);
        }else {
            holder.insertAbleGridView.setAlpha(1.0f);
            holder.tvName.setAlpha(1.0f);
            holder.loadStatusIv.setVisibility(View.GONE);
            holder.loadStatusIv.setImageDrawable(null);
            holder.loadStatusCircleProgressView.setVisibility(View.GONE);
        }
    }

    private void startAnimation(ImageView iv,long duration,int times) {
        if(iv == null) return;
        Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        trans.setDuration(duration);
        trans.setRepeatCount(times);
        iv.startAnimation(trans);
    }

    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        super.onBindSubViewHolder(holder, mainPosition, subPosition);
        if(subPosition < mData.get(mainPosition).size()){
            if(mData.get(mainPosition).get(subPosition) == null){
                holder.deleteIv.setVisibility(View.GONE);
                holder.tvName.setVisibility(View.GONE);
                holder.tvName.setText(context.getString(R.string.add));
                holder.insertAbleGridView.setAlpha(1.0f);
                holder.loadStatusIv.setVisibility(View.GONE);
                holder.loadStatusIv.setImageDrawable(null);
                holder.loadStatusCircleProgressView.setVisibility(View.GONE);
            }else {
                showDelete = preferences.getBoolean(MyConfigs.SHOWDELETE,false);
                if(showDelete) {
                    holder.deleteIv.setVisibility(mData.get(mainPosition).get(subPosition).getCanuninstalled() == 1 ? View.VISIBLE : View.GONE);
                }else {
                    holder.deleteIv.setVisibility(View.GONE);
                }
                holder.tvName.setVisibility(View.VISIBLE);
//                holder.tvName.setText(mData.get(mainPosition).get(subPosition).getName());
                //??????????????????
                List<String> installedPackages = new ArrayList<>();
                installedPackages.add(mData.get(mainPosition).get(subPosition).getPackageName());
                setDownloadStatus(false,holder,mData.get(mainPosition).get(subPosition).getInstalled(),
                        mData.get(mainPosition).get(subPosition).getStatus(),installedPackages);
                holder.tvName.setText(getName(mData.get(mainPosition).get(subPosition).getInstalled(),
                        mData.get(mainPosition).get(subPosition).getName()));
                Log.d("hqtest","onBindSubViewHolder package is: " + mData.get(mainPosition).get(subPosition).getPackageName() + ",parent = " + mainPosition + ",child = " + subPosition);
            }
        }
    }

    @Override
    public View getView(ViewGroup parent, int mainPosition, int subPosition) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner,parent,false);
        ImageView iconIv = (ImageView) view.findViewById(R.id.icon_iv);
        //iconIv.setScaleType(ImageView.ScaleType.FIT_XY);
        if(mainPosition < mData.size() && subPosition < mData.get(mainPosition).size()){
            if(mData.get(mainPosition).get(subPosition) == null){
                iconIv.setImageResource(R.drawable.add_app_icon_new);
            }else{
                Drawable drawable;
                if(null == mData.get(mainPosition).get(subPosition).getImgDrawable()){
                    byte[] b = mData.get(mainPosition).get(subPosition).getImgByte();
                    drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
                }else {
                    drawable = mData.get(mainPosition).get(subPosition).getImgDrawable();
                }
                if(drawable != null){
                    if(!AppLists.isSystemApplication(context,mData.get(mainPosition).get(subPosition).getPackageName())){
                        RoundCornerImage.crop(context,drawable,iconIv);
                    }else {
                        iconIv.setImageDrawable(drawable);
                    }
                }else {
                    Log.d(TAG,"set default ic_launcher,pkg: " + mData.get(mainPosition).get(subPosition).getPackageName());
                    drawable = context.getDrawable(R.drawable.ic_launcher);
                    iconIv.setImageDrawable(drawable);
                }
            }
        }else {
            Log.d(TAG,"index error,set default ic_launcher");
            iconIv.setImageDrawable(context.getDrawable(R.drawable.ic_launcher));
        }

        return view;
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        //Toast.makeText(view.getContext(),"x: "+parentIndex+"\nindex: "+index,Toast.LENGTH_SHORT).show();

        if(mData != null && mData.size() <= parentIndex){
            Log.d("MyAppInfoAdapter","parentIndex error");
            //???????????????
            List<LocationBean> lists;
            for(int k = 0; k < mData.size(); k++) {
                lists = mData.get(k);
                if (lists == null) continue;//????????? ?????????????????????
                for (int i = 0; i < lists.size(); i++) {
                    locationBean = lists.get(i);
                    if (locationBean == null) continue;
                    locationBean.printLog();
                }
            }
            return;
        }

        RelativeLayout relativeLayout = (RelativeLayout) view;
//        ImageView iv = (ImageView) relativeLayout.getChildAt(2);
//        TextView tv = (TextView) relativeLayout.getChildAt(3);
//        if(tv.getText().toString().trim().equals(context.getString(R.string.add))){
//            if(isTimeEnabled()){//????????????
//                editor.putBoolean(MyConfigs.SHOWDELETE,false);
//                editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
//                editor.commit();
//                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
//                showAddDialog(parentIndex);
//            }
//        }else if(tv.getText().toString().trim().equals(context.getString(R.string.appmanagement_name))){
//            if(isTimeEnabled()) {//????????????
//                editor.putBoolean(MyConfigs.SHOWDELETE,false);
//                editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
//                editor.commit();
//                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
//                //showAppManagementDialog();
//                AppManagementWindow.getInstance(context).show();
//            }
//        }else {
//            if(isTimeEnabled()) {//????????????
//                String pkgName = "";
//                if(index == -1){
//                    pkgName = mData.get(parentIndex).get(0).getPackageName();
//                }else {
//                    pkgName = mData.get(parentIndex).get(index).getPackageName();
//                }
//                boolean isSystemApp = AppLists.isSystemApplication(context,pkgName);
//                Log.d("MyAppInfoAdapter","onItemClick isClickDelete: " + isClickDelete + ",isSystemApp: " + isSystemApp +
//                        "???package: " + pkgName);
//                if(isClickDelete && !isSystemApp){//????????????????????????
//                    //hideDeleteIcon((RecyclerView) relativeLayout.getParent());
//                    //resetDeleteFlag(true,parentIndex);//position??????-1??????,???parentIndex
//
//                    if(index == -1){//main???
//                        editor.putBoolean(MyConfigs.MAINSHOWDELETE,true);
//                        editor.commit();
//                        showDeleteDialog(mData.get(parentIndex).get(0).getName(),pkgName,true);
//                    }else {//sub???
//                        editor.putBoolean(MyConfigs.SHOWDELETE,true);
//                        editor.commit();
//                        showDeleteDialog(mData.get(parentIndex).get(index).getName(),pkgName,false);
//                    }
//                }else {
//                    editor.putBoolean(MyConfigs.SHOWDELETE,false);
//                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
//                    editor.commit();
//                    hideDeleteIcon((RecyclerView) relativeLayout.getParent());
//
//                    if(index == -1){//-1 ???main area
//                        doClick(mData.get(parentIndex).get(0));
//                    }else {
//                        if(mData.get(parentIndex).get(index) != null){
//                            doClick(mData.get(parentIndex).get(index));
//                        }else {
//                            Log.d("MyAppInfoAdapter","get location is null");
//                            return;
//                        }
//                    }
//    //                Utils.launchApp(context,packageName);
//                }
//            }else {
//                Log.d("MyAppInfoAdapter","click too fast,ignore");
//            }
//        }
        commandItemClick((RecyclerView) relativeLayout.getParent(),parentIndex,index);
    }

    private void commandItemClick(RecyclerView relativeLayout,int parentIndex, int index){
        if(isTimeEnabled()) {//????????????
            String pkgName = "";
            if(index == -1){
                locationBean = mData.get(parentIndex).get(0);
            }else {
                locationBean = mData.get(parentIndex).get(index);
            }

            if(locationBean == null){//????????????
                if(isTimeEnabled()){//????????????
                    editor.putBoolean(MyConfigs.SHOWDELETE,false);
                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                    editor.commit();
                    hideDeleteIcon(relativeLayout);
                    showAddDialog(parentIndex);
                }
            }else if(AppLists.APPMANAGEMENT.equals(locationBean.getPackageName())){//????????????
                if(isTimeEnabled()) {//????????????
                    editor.putBoolean(MyConfigs.SHOWDELETE,false);
                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                    editor.commit();
                    hideDeleteIcon(relativeLayout);
                    if(onItemClickCallback != null){
                        onItemClickCallback.onItemClick();
                    }
                    AppManagementWindow.getInstance(context).show();
                }
            }else {
                if(isTimeEnabled()) {//????????????
                    pkgName = locationBean.getPackageName();
                    boolean isSystemApp = AppLists.isSystemApplication(context,pkgName);
                    Log.d("MyAppInfoAdapter","onItemClick isClickDelete: " + isClickDelete + ",isSystemApp: " + isSystemApp +
                            "???package: " + pkgName);
                    if(isClickDelete && !isSystemApp){//????????????????????????
                        if(index == -1){//main???
                            editor.putBoolean(MyConfigs.MAINSHOWDELETE,true);
                            editor.commit();
                            showDeleteDialog(locationBean.getName(),pkgName,true);
                        }else {//sub???
                            editor.putBoolean(MyConfigs.SHOWDELETE,true);
                            editor.commit();
                            showDeleteDialog(locationBean.getName(),pkgName,false);
                        }
                    }else {
                        editor.putBoolean(MyConfigs.SHOWDELETE,false);
                        editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                        editor.commit();
                        hideDeleteIcon(relativeLayout);

                        doClick(locationBean);
                    }
                }else {
                    Log.d("MyAppInfoAdapter","click too fast,ignore");
                }
            }
        }else {
            Log.d("MyAppInfoAdapter","click too fast,ignore");
        }
    }

    private void doClick(LocationBean locationBean){
        int installed = locationBean.getInstalled();
        String pkgName = locationBean.getPackageName();
        String name = locationBean.getName();
        Log.d("MyAppInfoAdapter","doClick installed = " + installed);
        if(installed == AppState.DOWNLOAD_PAUSED || installed == AppState.DOWNLOAD_FAIL){//???????????????????????????
            AppStoreService.getInstance(context).doCommand(CommandType.RESUME,pkgName);
        }else if(installed == AppState.DOWNLOADING){//?????????
            AppStoreService.getInstance(context).doCommand(CommandType.PAUSE,pkgName);
        }else if(installed == 0 || installed == AppState.INSTALLED || installed == AppState.INSTALLED_COMPLETELY){//?????????
            if(onItemClickCallback != null){
                onItemClickCallback.onItemClick();
            }
            RecentAppHelper.launchApp(context,pkgName);
        }else if(installed == AppState.COULD_UPDATE){//??????
            /*
            *  ??????????????????????????????????????????APP?????????????????????????????????????????????????????????????????????????????????
            *  ???????????????reverse3>reverse1??????reverse3 ???= reverse2????????????????????????
             */
            //????????????????????????
            String reverse1 = locationBean.getReserve1();//????????????????????????
            String reverse2 = locationBean.getReserve2();//????????????????????????
            String reverse3 = locationBean.getReserve3();//?????????????????????
            if(TextUtils.isEmpty(reverse3)){
                if(onItemClickCallback != null){
                    onItemClickCallback.onItemClick();
                }
                RecentAppHelper.launchApp(context,pkgName);
            }else {
                if(!TextUtils.isEmpty(reverse1)){
                    if(Integer.parseInt(reverse3) > Integer.parseInt(reverse1)){
                        //????????? ???????????????????????????
                        if(!TextUtils.isEmpty(reverse2) &&
                                Integer.parseInt(reverse3) == Integer.parseInt(reverse2)){
                            if(onItemClickCallback != null){
                                onItemClickCallback.onItemClick();
                            }
                            RecentAppHelper.launchApp(context,pkgName);
                        }else {//????????? ??????????????????
                            showUpdateDialog(name,pkgName,reverse3);
                        }
                    }else {
                        if(onItemClickCallback != null){
                            onItemClickCallback.onItemClick();
                        }
                        RecentAppHelper.launchApp(context,pkgName);
                    }
                }else {
                    if(onItemClickCallback != null){
                        onItemClickCallback.onItemClick();
                    }
                    RecentAppHelper.launchApp(context,pkgName);
                }
            }
        }else if(installed == AppState.INSTALLING){//?????????
            //????????????????????????
            Log.d("MyAppInfoAdapter","doClick INSTALLING...");
        }else {
            if(onItemClickCallback != null){
                onItemClickCallback.onItemClick();
            }
            RecentAppHelper.launchApp(context,pkgName);
        }
    }

    private void showAddDialog(int parentIndex){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.add_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);

        TextView positiveTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_positive_tv);
        TextView negativeTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_negative_tv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_title);

        RecyclerView rv = (RecyclerView) dialog.getWindow().findViewById(R.id.add_recyclerview);
        List<LocationBean> initlists = getAddAppLists(parentIndex,titleTv);//?????????titleTv?????????
        AddAppAdapter addAppAdapter = new AddAppAdapter(context,initlists,parentIndex,new SelectedCallback(){

            @Override
            public void onSelect(String selectContent) {
                titleTv.setText(context.getString(R.string.add_dialog_title,selectContent));
            }
        });
        rv.setAdapter(addAppAdapter);
        rv.setLayoutManager(new GridLayoutManager(context, 3));

        //????????????????????????sub????????????0???1???????????????????????????
        editor.putBoolean(MyConfigs.SHOWDELETE,false);
        editor.commit();

        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                /*
                *  ?????????
                *  ????????? ?????????????????????0???
                *        ????????????sub???????????????????????????parentIndex?????????????????????????????????????????????,??????sub
                *  ????????? ?????????????????????1???
                *        1.??????????????????sub??????????????????
                *          ?????????????????????????????????????????????sub????????????????????????????????????????????????sub
                *        2.?????????????????????sub??????????????????
                *          ???????????????????????????????????????????????????sub??????????????????????????????????????????????????????????????????sub???????????????????????????????????????,??????sub
                *  ????????? ????????????????????????1??????
                *        1.???sub??????????????????????????? ??????????????? ?????????????????????
                *           ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????sub
                *        2.???sub??????????????????????????? ?????? ?????????????????????
                *           ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????sub
                 */
                List<LocationBean> selectedLists = addAppAdapter.getSelectdItems();//?????????????????????list
                List<LocationBean> childLists = mData.get(parentIndex);
                //??????SubRecyclerView???Inconsistency detected?????????????????????notifyDataSetChanged????????????????????????
                //childLists.removeAll(Collections.singleton(null));
                //???????????????????????????????????????
                if(childLists.size() - 1 == selectedLists.size() && childLists.containsAll(selectedLists)){
                    return;
                }
                if(selectedLists.size() == 0){
                    EventBus.getDefault().post(new HideSubContainerEvent());//??????ClassifyView??????subContainer
                    //???sub???????????????????????????parentIndex?????????????????????????????????????????????
                    LocationBean lb = mData.get(parentIndex).get(0);
                    mData.remove(parentIndex);
                    lb.setParentIndex(parentIndex);
                    lb.setChildIndex(-1);
                    List<LocationBean> newList = new ArrayList<>();
                    newList.add(lb);
                    mData.add(parentIndex,newList);

                    List<LocationBean> subLists = getSubAdapter().getSubData();//?????????sub????????????list
                    subLists.removeAll(Collections.singleton(null));//?????????null??????
                    subLists.remove(0);
                    for (int m = 0; m < subLists.size(); m++) {
                        List<LocationBean> newList1 = new ArrayList<>();
                        LocationBean locationBean = subLists.get(m);
                        locationBean.setParentIndex(mData.size());
                        locationBean.setChildIndex(-1);
                        newList1.add(locationBean);
                        mData.add(newList1);
                    }
                    notifyDataSetChanged();
                }else if(selectedLists.size() == 1){
                    EventBus.getDefault().post(new HideSubContainerEvent());//??????ClassifyView??????subContainer
                    LocationBean lb = selectedLists.get(0);
                    int originPosition = lb.getParentIndex();
                    if(mData.get(parentIndex).contains(lb)){//???????????????
                        mData.remove(parentIndex);
                        lb.setParentIndex(parentIndex);
                        lb.setChildIndex(-1);
                        List<LocationBean> newList = new ArrayList<>();
                        newList.add(lb);
                        mData.add(parentIndex,newList);

                        List<LocationBean> subLists = getSubAdapter().getSubData();
                        subLists.removeAll(Collections.singleton(null));//?????????null??????
                        //????????????lb????????????subLists????????????????????????
                        for(LocationBean locationBean:subLists){
                            if(locationBean != null && locationBean.getPackageName().equals(lb.getPackageName())){
                                subLists.remove(locationBean);
                                break;
                            }
                        }
                        //?????????subLists???????????????????????????
                        for (int m = 0; m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean = subLists.get(m);
                            locationBean.setParentIndex(mData.size());
                            locationBean.setChildIndex(-1);
                            newList1.add(locationBean);
                            mData.add(newList1);
                        }
                    }else {//?????????????????????
                        mData.remove(parentIndex);
                        lb.setParentIndex(parentIndex);
                        lb.setChildIndex(-1);
                        List<LocationBean> newList = new ArrayList<>();
                        newList.add(lb);
                        mData.add(parentIndex,newList);

                        List<LocationBean> subLists = getSubAdapter().getSubData();
                        subLists.removeAll(Collections.singleton(null));//?????????null??????
                        LocationBean locationBean = subLists.get(0);//????????????subLists???????????????????????????????????????????????????????????????
                        locationBean.setParentIndex(originPosition);
                        locationBean.setChildIndex(-1);
                        List<LocationBean> childList = new ArrayList<>();
                        childList.add(locationBean);
                        mData.remove(originPosition);
                        mData.add(originPosition,childList);

                        subLists.remove(0);//??????????????????
                        //?????????subLists???????????????????????????
                        for (int m = 0; m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean1 = subLists.get(m);
                            locationBean1.setParentIndex(mData.size());
                            locationBean1.setChildIndex(-1);
                            newList1.add(locationBean1);
                            mData.add(newList1);
                        }
                    }
                    notifyDataSetChanged();
                }else {//????????????????????????1??????
                    List<LocationBean> subLists = getSubAdapter().getSubData();
                    subLists.removeAll(Collections.singleton(null));//?????????null??????

                    //??????????????????????????????????????????mData???ParentIndex???ChildIndex??????????????????????????????
                    for(int i = 0; i < mData.size(); i++){
                        List<LocationBean> lists = mData.get(i);
                        if(lists != null && lists.size() == 1){
                            lists.get(0).setParentIndex(i);
                            lists.get(0).setChildIndex(-1);
                        }
                    }

                    //selectedLists?????????sub???????????????originItems???
                    List<LocationBean> originItems = new ArrayList<>();
                    for(LocationBean lb : subLists){
                        if(selectedLists.contains(lb)){
                            originItems.add(lb);
                        }
                    }

                    //?????????originItems????????????selectedLists???????????????
                    selectedLists.removeAll(originItems);
                    //?????????originItems????????????subLists??????sub??????????????????????????????
                    subLists.removeAll(originItems);
                    LocationBean originlb;//sub???????????????bean
                    LocationBean newlb;//???????????????bean
                    List<LocationBean> currentLists = null;
                    //???????????????????????? ??????????????? ?????????????????????
                    if(subLists.size() >= selectedLists.size()){
                        //????????????????????????????????????????????????
                        for(int i = 0; i < selectedLists.size(); i++){
                            newlb = selectedLists.get(i);
                            List<LocationBean> targetLists = mData.get(newlb.getParentIndex());
                            targetLists.clear();
                            originlb = subLists.get(i);
                            originlb.setParentIndex(newlb.getParentIndex());
                            originlb.setChildIndex(-1);
                            originlb.setTitle("");
                            targetLists.add(originlb);
                            mData.set(newlb.getParentIndex(),targetLists);
                        }

                        //?????????subLists???????????????????????????
                        for (int m = selectedLists.size(); m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean1 = subLists.get(m);
                            locationBean1.setParentIndex(mData.size());
                            locationBean1.setChildIndex(-1);
                            newList1.add(locationBean1);
                            mData.add(newList1);
                        }

                        //??????selectedLists??????ParentIndex???ChildIndex
                        for (int i = 0; i < selectedLists.size(); i++) {
                            LocationBean lb = selectedLists.get(i);
                            lb.setParentIndex(parentIndex);
                            lb.setChildIndex(i);
                        }
                        currentLists = mData.get(parentIndex);
                        currentLists.clear();
                        currentLists.addAll(originItems);//?????????
                        currentLists.addAll(selectedLists);//?????????
                        //????????????????????????ChildIndex
                        for(int i = 0; i < currentLists.size(); i++){
                            currentLists.get(i).setChildIndex(i);
                        }
                        mData.set(parentIndex,currentLists);
                    }else {//???????????????????????? ?????? ?????????????????????
                        for(int i = 0; i < selectedLists.size(); i++){
                            //????????????????????????????????????????????????
                            if(i < subLists.size()){
                                newlb = selectedLists.get(i);
                                originlb = subLists.get(i);
                                originlb.setParentIndex(newlb.getParentIndex());
                                originlb.setChildIndex(-1);
                                originlb.setTitle("");
                                List<LocationBean> originLists = new ArrayList<>();
                                originLists.add(originlb);
                                mData.set(newlb.getParentIndex(),originLists);
                            }else {//??????????????????????????????
                                newlb = selectedLists.get(i);
                                List<LocationBean> originLists = new ArrayList<>();
                                mData.set(newlb.getParentIndex(),originLists);
                            }
                        }

                        //??????selectedLists??????ParentIndex???ChildIndex
                        for (int i = 0; i < selectedLists.size(); i++) {
                            LocationBean lb = selectedLists.get(i);
                            lb.setParentIndex(parentIndex);
                            lb.setChildIndex(i);
                        }
                        currentLists = mData.get(parentIndex);
                        currentLists.clear();
                        currentLists.addAll(originItems);//?????????
                        currentLists.addAll(selectedLists);//?????????
                        //????????????????????????ChildIndex
                        for(int i = 0; i < currentLists.size(); i++){
                            currentLists.get(i).setChildIndex(i);
                        }
                        mData.set(parentIndex,currentLists);
                    }

                    //??????mData???item???????????????
                    for(int i = 0; i < mData.size(); i++){
                        if(mData.get(i) != null && mData.get(i).size() == 0){
                            mData.remove(i);
                            i--;
                        }
                    }
                    notifyDataSetChanged();

                    //????????????????????????????????????????????????????????????????????????????????????parentIndex
                    int newIndex = 0;
                    for(int k =0; k < mData.size(); k++){
                        if(mData.get(k).get(0).getPackageName().equals(currentLists.get(0).getPackageName())){
                            newIndex = k;
                            break;
                        }
                    }
                    mData.get(newIndex).add(null);//??????????????????
                    getSubAdapter().initData(newIndex,mData.get(newIndex));//??????sub
                    editor.putInt(MyConfigs.PARENTINDEX,newIndex);
                    editor.commit();
                }
            }
        });
        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static long lastTimeMillis;
    private static final long MIN_CLICK_INTERVAL = 1000;
    //dialog??????
    protected boolean isTimeEnabled() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if (Math.abs(currentTimeMillis - lastTimeMillis) > MIN_CLICK_INTERVAL) {
//            lastTimeMillis = currentTimeMillis;
//            return true;
//        }
//        return false;
        return true;
    }

    private List<LocationBean> getAddAppLists(int parentIndex,TextView tv){
        List<LocationBean> addAppLists = new ArrayList<>();
        List<LocationBean> lists;
        addAppLists.addAll(mData.get(parentIndex));
        addAppLists.removeAll(Collections.singleton(null));//??????null
        selectSize = addAppLists.size();
        //?????????????????????????????????????????????parentIndex???
        for (LocationBean info:addAppLists) {
            if(null != info) info.setParentIndex(parentIndex);
        }
        for(int i = 0; i < mData.size(); i++){
            lists = mData.get(i);
            if(lists != null && lists.size() == 1){//????????????
                lists.get(0).setParentIndex(i);//?????????parentIndex???
                addAppLists.add(lists.get(0));
            }
        }

        tv.setText(context.getString(R.string.add_dialog_title,selectSize + "/" + addAppLists.size()));
        return addAppLists;
    }

    private void showDeleteDialog(String name,String packageName,boolean isSendCount){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.uninstall_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);
        TextView positiveTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_positive_tv);
        TextView negativeTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_negative_tv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_title);
        titleTv.setText(context.getString(R.string.uninstall_dialog_title,name));
        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInstalled = PropertyUtils.checkPkgInstalled(context,packageName);
                if(isInstalled){//?????????
                    uninstall(packageName);
                }else {//?????????
                    //???????????????????????????????????????????????????????????????
                    AppStoreService.getInstance(context).doCommand(CommandType.CANCEL,packageName);
                    EventBus.getDefault().post(new CancelDownloadEvent(packageName));
                }
                dialog.dismiss();
                if(isSendCount) EventBus.getDefault().post(new UninstallCommandEvent());//????????????????????????????????????
            }
        });
        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(isSendCount) EventBus.getDefault().post(new UninstallCommandEvent());//????????????????????????????????????
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showUpdateDialog(String name,String packageName,String reverse3){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.update_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);
        TextView openTv = (TextView) dialog.getWindow().findViewById(R.id.update_dialog_open_tv);
        TextView updateTv = (TextView) dialog.getWindow().findViewById(R.id.update_dialog_update_tv);
        ImageView selectIv = (ImageView) dialog.getWindow().findViewById(R.id.update_dialog_select_iv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.update_dialog_title);
        titleTv.setText(context.getString(R.string.update_dialog_title,name));
        openTv.setText(context.getString(R.string.update_dialog_open,"5"));
        openTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null){
                    timer.cancel();
                }
                dialog.dismiss();
                if(selectIv.isSelected()){
                    EventBus.getDefault().post(new NotRemindEvent(packageName,reverse3));
                }

                if(onItemClickCallback != null){
                    onItemClickCallback.onItemClick();
                }
                RecentAppHelper.launchApp(context,packageName);
            }
        });
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null){
                    timer.cancel();
                }
                dialog.dismiss();
                if(selectIv.isSelected()){
                    EventBus.getDefault().post(new NotRemindEvent(packageName,reverse3));
                }
                //??????????????????
                AppStoreService.getInstance(context).doCommand(CommandType.UPDATE,packageName);
            }
        });
        selectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectIv.isSelected()){
                    selectIv.setSelected(false);
                }else {
                    selectIv.setSelected(true);
                }
            }
        });
        dialog.setCancelable(false);

        //?????????5s??????,????????????dialog??????1s????????????????????????6s
        timer = new CalcuTimer(6000L, 1000L, new CalcuTimerCallback() {
            @Override
            public void onCount(int seconds) {
                openTv.setText(context.getString(R.string.update_dialog_open,seconds + ""));
            }

            @Override
            public void onFinish() {
                openTv.callOnClick();
            }
        });
        dialog.show();
        timer.start();
    }

    // ??????APK
    private void uninstall(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
        PackageInstaller mPackageInstaller = context.getPackageManager().getPackageInstaller();
        //??????????????????app manifest???
        mPackageInstaller.uninstall(packageName, sender.getIntentSender());
    }

    private void hideDeleteIcon(RecyclerView recyclerView){
        RelativeLayout relativeLayout;
        InsertAbleGridView insertAbleGridView;
        for(int i = 0; i < recyclerView.getChildCount(); i++){
            relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
            if(insertAbleGridView.getChildCount() == 1){//????????????
                ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                iv.setVisibility(View.GONE);
            }
        }
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {
        public RelativeLayout rootRl;
        public TextView tvName;
        public ImageView deleteIv;
        public InsertAbleGridView insertAbleGridView;
        public RoundImageView loadStatusIv;
        public CircleProgressView loadStatusCircleProgressView;
        //????????????????????????????????????loadStatusIv????????????insertAbleGridView alpha??????
        //holder.insertAbleGridView.setAlpha(0.5f);
        //holder.loadStatusIv.setVisibility(View.VISIBLE);
        public ViewHolder(View itemView) {
            super(itemView);
            rootRl = (RelativeLayout) itemView.findViewById(R.id.root_rl);
            tvName = (TextView) itemView.findViewById(R.id.app_name_tv);
            deleteIv = (ImageView) itemView.findViewById(R.id.delete_iv);
            insertAbleGridView = (InsertAbleGridView) itemView.findViewById(R.id.insertAbleGridView);
            loadStatusIv = (RoundImageView) itemView.findViewById(R.id.load_status_iv);
            loadStatusIv.setRectAdius(15);//????????????
            loadStatusCircleProgressView = (CircleProgressView) itemView.findViewById(R.id.load_status_circleProgressView);

            rootRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MyAppInfoAdapter","rootRl click");
                    isClickDelete = false;
                }
            });
            deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MyAppInfoAdapter","deleteIv click");
                    isClickDelete = true;
                }
            });
        }
    }
}
