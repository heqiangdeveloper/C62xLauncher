package com.chinatsp.volcano.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.volcano.R;

public class BigCardViewHolder extends VolcanoViewHolder{
    private ImageView ivCardVolcanoLogin;
    private TextView tvCardVolcanoLogin;
    private RecyclerView rcvCardVolcanoVideoList;
    public BigCardViewHolder(View rootView) {
        super(rootView);
        ivCardVolcanoLogin = rootView.findViewById(R.id.ivCardVolcanoLogin);
        tvCardVolcanoLogin = rootView.findViewById(R.id.tvCardVolcanoLogin);
        rcvCardVolcanoVideoList = rootView.findViewById(R.id.rcvCardVolcanoVideoList);
    }

    @Override
    public void showNormal() {
        ivCardVolcanoLogin.setVisibility(View.GONE);
        tvCardVolcanoLogin.setVisibility(View.GONE);
        rcvCardVolcanoVideoList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDisconnect() {

    }

    @Override
    public void showLogin() {

    }
}
