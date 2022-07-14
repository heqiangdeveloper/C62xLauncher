package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.iquting.R;

public class NetWorkDisconnectState implements State{
    @Override
    public void updateViewState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setImageResource(R.drawable.card_icon_wifi_disconnect);
        tvCardIQuTingTip.setText("网络未连接");
        ivCardIQuTingButton.setImageResource(R.drawable.card_icon_left_in_normal  );

        ivCardIQuTingLogo.setVisibility(View.VISIBLE);
        tvCardIQuTingTip.setVisibility(View.VISIBLE);
        ivCardIQuTingButton.setVisibility(View.VISIBLE);
    }
}
