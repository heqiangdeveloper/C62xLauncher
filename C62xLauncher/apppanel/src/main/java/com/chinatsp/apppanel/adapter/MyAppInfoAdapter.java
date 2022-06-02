package com.chinatsp.apppanel.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anarchy.classifyview.simple.SimpleAdapter;
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

    public MyAppInfoAdapter(Context context, List<List<LocationBean>> mData) {
        super(mData);
        this.mData = mData;
        this.context = context;
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
        //Log.d("heqq","info size is: " + infos.size());
        if(infos != null && infos.size() > 1){
            if(!TextUtils.isEmpty(infos.get(0).getTitle())){
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

            for(int i = 0; i < infos.size(); i++){
                if(infos.get(i) == null){
                    continue;
                }
                LocationBean lb = infos.get(i);
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
            locationBean.setCanuninstalled(1);
            int num = db.isExistPackage(locationBean.getPackageName());
            Log.d("hqtest","package package is: " + mData.get(position).get(0).getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + -1);
            if(num == 0){
                db.insertLocation(locationBean);
            }else {
                db.updateTitle(locationBean);
            }
        }
        //Log.d("heqq","name is: " + holder.tvName.getText().toString());
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

    static class ViewHolder extends SimpleAdapter.ViewHolder {
        public TextView tvName;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.app_name_tv);
        }
    }
}
