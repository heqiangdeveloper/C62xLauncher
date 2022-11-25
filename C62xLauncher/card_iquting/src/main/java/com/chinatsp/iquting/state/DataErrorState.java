package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.iquting.R;

public class DataErrorState implements IQuTingState {
    @Override
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingLogin).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingErrorNet).setVisibility(View.VISIBLE);

        TextView tvCardIQuTingNetTip = view.findViewById(R.id.tvCardIQuTingNetTip);
        tvCardIQuTingNetTip.setText(R.string.iquting_get_data_error);
    }

    @Override
    public void updateBigCardState(View view) {
        TextView tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvCardIQuTingLoginTipBig);
        TextView tvIQuTingDailySongs = view.findViewById(R.id.tvIQuTingDailySongs);
        TextView tvIQuTingRankSongs = view.findViewById(R.id.tvIQuTingRankSongs);
        View widgetView = view.findViewById(R.id.layoutIQuTingPlayWidget);
        ImageView ivCardIQuTingRefreshBig = view.findViewById(R.id.ivCardIQuTingRefreshBig);
        if(tvCardIQuTingLoginTipBig != null){
            tvCardIQuTingLoginTipBig.setVisibility(View.VISIBLE);
            tvCardIQuTingLoginTipBig.setText(R.string.iquting_get_data_error);
        }
        if(tvIQuTingDailySongs != null) tvIQuTingDailySongs.setVisibility(View.GONE);
        if(tvIQuTingRankSongs != null) tvIQuTingRankSongs.setVisibility(View.GONE);

        if(view.findViewById(R.id.rcvCardIQuTingSongList) != null){
            view.findViewById(R.id.rcvCardIQuTingSongList).setVisibility(View.GONE);
        }
        if(widgetView != null) widgetView.setVisibility(View.GONE);
        if(ivCardIQuTingRefreshBig != null) ivCardIQuTingRefreshBig.setVisibility(View.VISIBLE);
    }
}
