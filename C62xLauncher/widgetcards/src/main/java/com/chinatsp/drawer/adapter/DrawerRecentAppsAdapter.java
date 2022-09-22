package com.chinatsp.drawer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import launcher.base.utils.recent.RecentAppHelper;

public class DrawerRecentAppsAdapter extends RecyclerView.Adapter<DrawerRecentAppsAdapter.ViewHolder>{
    private List<HashMap<String,Object>> recentAppInfos;
    private LayoutInflater layoutInflater;
    private Context context;

    public DrawerRecentAppsAdapter(Context context, List<HashMap<String,Object>> recentAppInfos) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.recentAppInfos = recentAppInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.drawer_item_recent_apps,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String packageName = (String) recentAppInfos.get(position).get("packageName");
        String appName = (String) recentAppInfos.get(position).get("title");
        holder.appNameTv.setText(appName);
        holder.appIconIv.setImageDrawable((Drawable) recentAppInfos.get(position).get("icon"));
        holder.appIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentAppHelper.launchApp(context,packageName);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return recentAppInfos.size();
        return recentAppInfos.size() > 4? 4 : recentAppInfos.size();
    }

    public void setData(List<HashMap<String, Object>> appInfos) {
        if (appInfos == null || appInfos.isEmpty()) {
            return;
        }
        if (recentAppInfos == null) {
            recentAppInfos = new LinkedList<>();
        } else {
            recentAppInfos.clear();
        }
        recentAppInfos.addAll(appInfos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconIv = (ImageView) itemView.findViewById(R.id.ivAppIcon);
            appNameTv = (TextView) itemView.findViewById(R.id.tvAppName);
        }
    }
}
