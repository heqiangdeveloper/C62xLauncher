package com.chinatsp.apppanel.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.simple.SimpleAdapter;
import com.anarchy.classifyview.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.util.MyConfigs;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.bean.InfoBean;
import com.chinatsp.apppanel.bean.LocationBean;
import com.chinatsp.apppanel.db.MyAppDB;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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
    private int parentIndex;
    private String title;
    private boolean showDelete;

    public MyAppInfoAdapter(Context context, List<List<LocationBean>> mData) {
        super(mData);
        this.mData = mData;
        this.context = context;
        preferences = context.getSharedPreferences(MyConfigs.APPPANELSP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        db = new MyAppDB(context);
        for(int i = 0; i < mData.size(); i++){
            infos = mData.get(i);
            titleLists.add(infos.get(0).getTitle());
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
        holder.tvName.setText("");
        Log.d("hqtest","info size is: " + infos.size());
        if(infos != null && infos.size() > 1){//文件夹
            parentIndex = preferences.getInt( MyConfigs.PARENTINDEX ,  -1 );
            title = preferences.getString( MyConfigs.TITLE ,  "" );
            if(position == parentIndex && !TextUtils.isEmpty(title)){
                titleStr = title;
                editor.putInt(MyConfigs.PARENTINDEX,-1);
                editor.putString(MyConfigs.TITLE,"");
                editor.commit();
            }else if(!TextUtils.isEmpty(infos.get(0).getTitle())){
                titleStr = infos.get(0).getTitle();
            }else {
                int index = getExistDirIndex(db.getAllTitles());
                if(index == -1){
                    titleStr = "文件夹";
                }else {
                    index++;
                    titleStr = "文件夹" + index;
                }
            }
            for(LocationBean locationBean : infos){
                if(locationBean != null){
                    locationBean.setTitle(titleStr);
                }
            }
            holder.tvName.setText(titleStr);
            holder.deleteIv.setVisibility(View.GONE);

            for(int i = 0; i < infos.size(); i++){
                if(infos.get(i) == null){
                    continue;
                }
                LocationBean lb = infos.get(i);
                holder.deleteIv.setTag(lb.getCanuninstalled());
//                locationBean.setParentIndex(position);
//                locationBean.setChildIndex(i);
                //infos.get(i).setTitle(titleStr);
//                locationBean.setPackageName(infos.get(i).getPackageName());
//
                Drawable drawable;
                if(null == lb.getImgDrawable()){
                    byte[] b = lb.getImgByte();
                    drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
                }else {
                    drawable = lb.getImgDrawable();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                lb.setImgByte(baos.toByteArray());
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
            holder.tvName.setText(mData.get(position).get(0).getName());
            holder.deleteIv.setTag(mData.get(position).get(0).getCanuninstalled());
            //是否显示删除按钮
            parentIndex = preferences.getInt(MyConfigs.SHOWDELETEPOSITION ,  -1);
            showDelete = preferences.getBoolean(MyConfigs.SHOWDELETE,false);
            if(parentIndex != -1 && parentIndex == position){
                if(showDelete){
                    holder.deleteIv.setVisibility((int)holder.deleteIv.getTag() == 1 ? View.VISIBLE : View.GONE);
                }else {
                    holder.deleteIv.setVisibility(View.GONE);
                }
                editor.putBoolean(MyConfigs.SHOWDELETE,false);
                editor.putInt(MyConfigs.SHOWDELETEPOSITION,-1);
                editor.commit();
            }

            locationBean = mData.get(position).get(0);
//            locationBean.setParentIndex(position);
//            locationBean.setChildIndex(-1);
            locationBean.setTitle("");
//            locationBean.setPackageName(mData.get(position).get(0).getPackageName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Drawable drawable;
            if(null == locationBean.getImgDrawable()){
                byte[] b = locationBean.getImgByte();
                drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
            }else {
                drawable = mData.get(position).get(0).getImgDrawable();
            }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            locationBean.setImgByte(baos.toByteArray());
//            locationBean.setName(mData.get(position).get(0).getName());
            locationBean.setAddBtn(0);
            locationBean.setStatus(0);
            locationBean.setPriority(0);
            locationBean.setInstalled(1);
//            locationBean.setCanuninstalled(1);
            int num = db.isExistPackage(locationBean.getPackageName());
            Log.d("hqtest","package package is: " + mData.get(position).get(0).getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + -1);
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
    *   -1没有“文件夹” 0有“文件夹” X“文件夹X”
     */
    private int getExistDirIndex(List<String> titleLists){
        List<String> titles = new ArrayList<>();
        int lastNumber = -1;
        for(String title : titleLists){
            if(title.startsWith("文件夹") && title.length() <= 5){
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

    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        super.onBindSubViewHolder(holder, mainPosition, subPosition);
        if(mData.get(mainPosition).get(subPosition) == null){
            holder.tvName.setText(context.getString(R.string.add));
        }else {
            holder.tvName.setText(mData.get(mainPosition).get(subPosition).getName());
            Log.d("hqtest","onBindSubViewHolder package is: " + mData.get(mainPosition).get(subPosition).getPackageName() + ",parent = " + mainPosition + ",child = " + subPosition);
        }
    }

    @Override
    public View getView(ViewGroup parent, int mainPosition, int subPosition) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner,parent,false);
        ImageView iconIv = (ImageView) view.findViewById(R.id.icon_iv);
        if(mData.get(mainPosition).get(subPosition) == null){
            iconIv.setImageResource(R.drawable.ic_add_black_24dp);
        }else{
            if(null == mData.get(mainPosition).get(subPosition).getImgDrawable()){
                byte[] b = mData.get(mainPosition).get(subPosition).getImgByte();
                iconIv.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
            }else {
                iconIv.setImageDrawable(mData.get(mainPosition).get(subPosition).getImgDrawable());
            }
        }

        return view;
    }

    /*
     *打开应用
     * @param packageName包名
     */
    private void launchApp(String packageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        //Toast.makeText(view.getContext(),"x: "+parentIndex+"\nindex: "+index,Toast.LENGTH_SHORT).show();

        RelativeLayout relativeLayout = (RelativeLayout) view;
        ImageView iv = (ImageView) relativeLayout.getChildAt(1);
        TextView tv = (TextView) relativeLayout.getChildAt(2);
        if(tv.getText().toString().trim().equals(context.getString(R.string.add))){
            showAddDialog(parentIndex);
        }else {
            if(iv.getVisibility() == View.VISIBLE){//如果删除按钮显示了，执行删除应用逻辑
                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
                showDeleteDialog(tv.getText().toString());
            }else {
                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
                String packageName = "";
                if(index == -1){//-1 是main area
                    packageName = mData.get(parentIndex).get(0).getPackageName();
                }else {
                    if(mData.get(parentIndex).get(index) != null){
                        packageName = mData.get(parentIndex).get(index).getPackageName();
                    }else {
                        return;
                    }
                }
                launchApp(packageName);
            }
        }
    }

    private void showAddDialog(int parentIndex){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.add_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);
        RecyclerView rv = (RecyclerView) dialog.getWindow().findViewById(R.id.add_recyclerview);
        rv.setAdapter(new AddAppAdapter(context,getAddAppLists(parentIndex)));
        rv.setLayoutManager(new GridLayoutManager(context, 3));

        TextView positiveTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_positive_tv);
        TextView negativeTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_negative_tv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_title);
        //titleTv.setText(context.getString(R.string.uninstall_dialog_title,name));
        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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

    private List<LocationBean> getAddAppLists(int parentIndex){
        List<LocationBean> addAppLists = new ArrayList<>();
        List<LocationBean> lists;
        addAppLists.addAll(mData.get(parentIndex));
        for(int i = 0; i < mData.size(); i++){
            lists = mData.get(i);
            if(lists != null && lists.size() == 1){//非文件夹
                addAppLists.add(lists.get(0));
            }
        }
        return addAppLists;
    }

    private void showDeleteDialog(String name){
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
                dialog.dismiss();
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

    private void hideDeleteIcon(RecyclerView recyclerView){
        RelativeLayout relativeLayout;
        InsertAbleGridView insertAbleGridView;
        for(int i = 0; i < recyclerView.getChildCount(); i++){
            relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
            if(insertAbleGridView.getChildCount() == 1){//非文件夹
                ImageView iv = (ImageView) relativeLayout.getChildAt(1);
                iv.setVisibility(View.GONE);
            }
        }
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {
        public TextView tvName;
        public ImageView deleteIv;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.app_name_tv);
            deleteIv = (ImageView) itemView.findViewById(R.id.delete_iv);
        }
    }
}
