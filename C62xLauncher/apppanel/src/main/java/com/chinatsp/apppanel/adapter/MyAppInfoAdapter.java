package com.chinatsp.apppanel.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anarchy.classifyview.simple.SimpleAdapter;
import com.chinatsp.apppanel.R;

import java.util.List;

public class MyAppInfoAdapter extends SimpleAdapter<ResolveInfo, MyAppInfoAdapter.ViewHolder> {
    public List<List<ResolveInfo>> mData;
    public Context context;
    public MyAppInfoAdapter(Context context, List<List<ResolveInfo>> mData) {
        super(mData);
        this.mData = mData;
        this.context = context;
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        super.onBindMainViewHolder(holder, position);
        List<ResolveInfo> infos = mData.get(position);
        holder.tvName.setText("");
        if(infos != null && infos.size() > 1){
            Log.d("heqq","info size is: " + infos.size());
            holder.tvName.setText("文件夹");
        } else {
            holder.tvName.setText(mData.get(position).get(0).loadLabel(context.getPackageManager()));
        }
    }

    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        super.onBindSubViewHolder(holder, mainPosition, subPosition);
        if(mData.get(mainPosition).get(subPosition).activityInfo == null){
            holder.tvName.setText(context.getString(R.string.add));
        }else {
            holder.tvName.setText(mData.get(mainPosition).get(subPosition).loadLabel(context.getPackageManager()));
        }
    }

    @Override
    public View getView(ViewGroup parent, int mainPosition, int subPosition) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner,parent,false);
        ImageView iconIv = (ImageView) view.findViewById(R.id.icon_iv);
        if(mData.get(mainPosition).get(subPosition).activityInfo == null){
            iconIv.setImageResource(R.drawable.ic_add_black_24dp);
        }else{
            iconIv.setImageDrawable(mData.get(mainPosition).get(subPosition).activityInfo.loadIcon(context.getPackageManager()));
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
            packageName = mData.get(parentIndex).get(0).activityInfo.packageName;
        }else {
            if(mData.get(parentIndex).get(index).activityInfo != null){
                packageName = mData.get(parentIndex).get(index).activityInfo.packageName;
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
