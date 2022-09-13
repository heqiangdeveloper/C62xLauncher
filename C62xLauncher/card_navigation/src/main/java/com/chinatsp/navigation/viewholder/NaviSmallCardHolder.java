package com.chinatsp.navigation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.autonavi.autoaidlwidget.AutoAidlWidgetView;
import com.chinatsp.navigation.R;

public class NaviSmallCardHolder extends NaviCardHolder{
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviArrow;
    private AutoAidlWidgetView surfaceViewNavi;
    public NaviSmallCardHolder(View rootView) {
        super(rootView);
        ivCardNaviSearch = rootView.findViewById(R.id.ivCardNaviSearch);
        ivCardNaviHome = rootView.findViewById(R.id.ivCardNaviHome);
        ivCardNaviCompany = rootView.findViewById(R.id.ivCardNaviCompany);
        ivCardNaviArrow = rootView.findViewById(R.id.ivCardNaviArrow);
        surfaceViewNavi = rootView.findViewById(R.id.surfaceViewNavi);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {

    }

    @Override
    public void refreshFreeMode() {

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivCardNaviCompany) {
                naviToCompany();
            } else if (v == ivCardNaviSearch) {
                toSearch();
            } else if (v == ivCardNaviHome) {
                naviToHome();
            }
        }
    };

    private void toSearch() {
        Toast.makeText(mContext, "搜索地址", Toast.LENGTH_SHORT).show();
    }

    private void naviToCompany() {
        Toast.makeText(mContext, "导航去公司", Toast.LENGTH_SHORT).show();
    }

    private void naviToHome() {
        Toast.makeText(mContext, "导航回家", Toast.LENGTH_SHORT).show();
    }
}
