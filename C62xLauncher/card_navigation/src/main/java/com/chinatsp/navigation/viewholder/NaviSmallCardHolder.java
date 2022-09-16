package com.chinatsp.navigation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autonavi.autoaidlwidget.AutoAidlWidgetView;
import com.chinatsp.navigation.R;

public class NaviSmallCardHolder extends NaviCardHolder{
    private ImageView ivCardNaviSearch;
    private ImageView ivCardNaviHome;
    private ImageView ivCardNaviCompany;
    private ImageView ivCardNaviArrow;
    private ImageView ivCardNaviInstruction;
    private TextView tvCardNaviInstruction;
    private AutoAidlWidgetView surfaceViewNavi;
    public NaviSmallCardHolder(View rootView) {
        super(rootView);
        ivCardNaviSearch = rootView.findViewById(R.id.ivCardNaviSearch);
        ivCardNaviHome = rootView.findViewById(R.id.ivCardNaviHome);
        ivCardNaviCompany = rootView.findViewById(R.id.ivCardNaviCompany);
        ivCardNaviArrow = rootView.findViewById(R.id.ivCardNaviArrow);
        ivCardNaviInstruction = rootView.findViewById(R.id.ivCardNaviInstruction);
        tvCardNaviInstruction = rootView.findViewById(R.id.tvCardNaviInstruction);
        surfaceViewNavi = rootView.findViewById(R.id.surfaceViewNavi);

        ivCardNaviSearch.setOnClickListener(mOnClickListener);
        ivCardNaviHome.setOnClickListener(mOnClickListener);
        ivCardNaviCompany.setOnClickListener(mOnClickListener);
    }

    @Override
    public void refreshNavigation() {
        ivCardNaviArrow.setVisibility(View.VISIBLE);
        ivCardNaviCompany.setVisibility(View.VISIBLE);
        ivCardNaviHome.setVisibility(View.VISIBLE);
        ivCardNaviSearch.setVisibility(View.VISIBLE);
        ivCardNaviInstruction.setVisibility(View.GONE);
        tvCardNaviInstruction.setVisibility(View.GONE);
    }

    @Override
    public void refreshFreeMode() {
        ivCardNaviArrow.setVisibility(View.GONE);
        ivCardNaviCompany.setVisibility(View.GONE);
        ivCardNaviHome.setVisibility(View.GONE);
        ivCardNaviSearch.setVisibility(View.GONE);
        ivCardNaviInstruction.setVisibility(View.VISIBLE);
        tvCardNaviInstruction.setVisibility(View.VISIBLE);
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
