package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.drivecounselor.DriveCounselorCardView;
import com.chinatsp.econnect.EConnectCardView;

public class EConnectEntity extends BaseCardEntity{

    @Override
    public View getLayout(Context context) {
        return new EConnectCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new EConnectCardView(context);
    }
}
