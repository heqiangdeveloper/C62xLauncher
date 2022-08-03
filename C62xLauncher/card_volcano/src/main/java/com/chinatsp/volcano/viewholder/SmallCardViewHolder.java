package com.chinatsp.volcano.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.volcano.R;

public class SmallCardViewHolder extends VolcanoViewHolder{
    private ImageView ivCardVolcanoLogo;
    private TextView tvCardVolcanoVideoArtist;
    private TextView tvCardVolcanoVideoName;

    public SmallCardViewHolder(View rootView) {
        super(rootView);
        ivCardVolcanoLogo = rootView.findViewById(R.id.ivCardVolcanoLogo);
        tvCardVolcanoVideoArtist = rootView.findViewById(R.id.tvCardVolcanoVideoArtist);
        tvCardVolcanoVideoName = rootView.findViewById(R.id.tvCardVolcanoVideoName);
    }

    @Override
    public void showNormal() {

    }

    @Override
    public void showDisconnect() {

    }

    @Override
    public void showLogin() {

    }
}
