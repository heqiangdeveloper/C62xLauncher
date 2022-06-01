package com.chinatsp.entity;

import android.content.Context;
import android.view.View;

import com.chinatsp.drivecounselor.DriveCounselorCardView;

public class DriveCounselorEntity extends BaseCardEntity{

    @Override
    public View getLayout(Context context) {
        return new DriveCounselorCardView(context);
    }

    @Override
    public View getLargeLayout(Context context) {
        return new DriveCounselorCardView(context);
    }
}
