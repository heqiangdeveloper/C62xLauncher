package com.chinatsp.navigation.viewholder.lane;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chinatsp.navigation.R;
import com.chinatsp.navigation.gaode.bean.TrafficLaneModel;
import com.chinatsp.navigation.repository.LaneInfoIconFinder;

import launcher.base.recyclerview.BaseViewHolder;

public class LaneViewHolder extends BaseViewHolder<TrafficLaneModel.LaneInfo> {
    private ImageView ivCardNaviLaneIcon;

    public LaneViewHolder(@NonNull View itemView) {
        super(itemView);
        ivCardNaviLaneIcon = itemView.findViewById(R.id.ivCardNaviLaneIcon);
    }

    @Override
    public void bind(int position, TrafficLaneModel.LaneInfo laneInfo) {
        super.bind(position, laneInfo);
        ivCardNaviLaneIcon.setImageResource(LaneInfoIconFinder.findIconResById(laneInfo.getTrafficLaneIcon(), laneInfo.isTrafficLaneAdvised()));
    }
}
