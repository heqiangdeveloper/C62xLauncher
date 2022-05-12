package com.chinatsp.apppanel.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chinatsp.apppanel.R;

import java.util.List;

public class AppInfoAdapter extends BaseAdapter {
    private Context context;
    private List<ResolveInfo> infos;
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    public AppInfoAdapter(Context context, List<ResolveInfo> infos) {
        this.context = context;
        this.infos = infos;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.app_info_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_iv);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.root = (LinearLayout) convertView.findViewById(R.id.container_ll);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageDrawable(infos.get(position).activityInfo.loadIcon(context.getPackageManager()));
        viewHolder.name.setText(infos.get(position).loadLabel(context.getPackageManager()));
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(infos.get(position).activityInfo.packageName);
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        LinearLayout root;
        ImageView icon;
        TextView name;
    }

    /*
    *打开应用
    * @param packageName包名
     */
    private void launchApp(String packageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }
}
