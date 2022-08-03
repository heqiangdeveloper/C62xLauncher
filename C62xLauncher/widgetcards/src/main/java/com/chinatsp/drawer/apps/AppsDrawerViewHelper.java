package com.chinatsp.drawer.apps;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.adapter.DrawerRecentAppsAdapter;
import com.chinatsp.drawer.decoration.RecentAppsDecoration;
import com.chinatsp.widgetcards.R;

import java.util.HashMap;
import java.util.List;

import launcher.base.utils.recent.RecentAppHelper;

public class AppsDrawerViewHelper {
    private RecyclerView recentAppsRcv;
    private TextView recentAppsTv;
    private Context context;

    public AppsDrawerViewHelper(View rootView) {
        recentAppsTv = rootView.findViewById(R.id.tvRecentApps);
        recentAppsRcv = rootView.findViewById(R.id.rcvRecentApps);
        this.context = recentAppsRcv.getContext();
        refreshUI();
    }

    private void refreshUI() {
        List<HashMap<String,Object>> appInfos = RecentAppHelper.getRecentApps(context,20);
        if(appInfos.size() == 0){
            recentAppsRcv.setVisibility(View.GONE);
            recentAppsTv.setVisibility(View.VISIBLE);
        }else {
            recentAppsRcv.setVisibility(View.VISIBLE);
            recentAppsTv.setVisibility(View.GONE);
            DrawerRecentAppsAdapter recentAppsAdapter = new DrawerRecentAppsAdapter(context,
                    RecentAppHelper.getRecentApps(context,20));
            recentAppsRcv.setAdapter(recentAppsAdapter);
            //recentAppsRcv.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recentAppsRcv.setLayoutManager(new GridLayoutManager(context, 2));
            recentAppsRcv.addItemDecoration(new RecentAppsDecoration(15,20));
        }
    }
}
