package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.iquting.R;

public class NormalState implements IQuTingState {
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingErrorNet).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingLogin).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.VISIBLE);
    }

    public void updateBigCardState(View view) {
        View tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvCardIQuTingLoginTipBig);
        TextView tvIQuTingDailySongs = view.findViewById(R.id.tvIQuTingDailySongs);
        TextView tvIQuTingRankSongs = view.findViewById(R.id.tvIQuTingRankSongs);
        ImageView ivCardIQuTingRefreshBig = view.findViewById(R.id.ivCardIQuTingRefreshBig);
//        ConstraintLayout cl = view.findViewById(R.id.rootLayout);
//        if(cl != null) cl.setVisibility(View.VISIBLE);
        View widgetView = view.findViewById(R.id.layoutIQuTingPlayWidget);
        if(widgetView != null) widgetView.setVisibility(View.VISIBLE);
        tvCardIQuTingLoginTipBig.setVisibility(View.GONE);
        if(tvIQuTingDailySongs != null) tvIQuTingDailySongs.setVisibility(View.VISIBLE);
        if(tvIQuTingRankSongs != null) tvIQuTingRankSongs.setVisibility(View.VISIBLE);
        if(ivCardIQuTingRefreshBig != null) ivCardIQuTingRefreshBig.setVisibility(View.GONE);

        RecyclerView rcvCardIQuTingSongList = view.findViewById(R.id.rcvCardIQuTingSongList);
        rcvCardIQuTingSongList.setVisibility(View.VISIBLE);
    }

}
