package com.chinatsp.drawer.drive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.driveinfo.DriveInfo;
import com.chinatsp.widgetcards.R;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

public class DrawerDriveCounselorHolder extends BaseViewHolder<DrawerEntity> {
    private final DriveInfoDrawerController mController;

    private final TextView tvDrawerDriveHealthy;
    private final TextView tvDrawerDriveDistance;
    private final TextView tvDrawerDriveDistanceLabel;
    private final TextView tvDrawerDriveRanking;
    private final DistanceCircleProgress progressDrawerDriveDistance;


    public DrawerDriveCounselorHolder(@NonNull View itemView) {
        super(itemView);
        mController = new DriveInfoDrawerController(this);
        tvDrawerDriveHealthy = itemView.findViewById(R.id.tvDrawerDriveHealthy);
        tvDrawerDriveDistance = itemView.findViewById(R.id.tvDrawerDriveDistance);
        tvDrawerDriveDistanceLabel = itemView.findViewById(R.id.tvDrawerDriveDistanceLabel);
        tvDrawerDriveRanking = itemView.findViewById(R.id.tvDrawerDriveRanking);
        progressDrawerDriveDistance = itemView.findViewById(R.id.progressDrawerDriveDistance);
        mController.readDriveInfo();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentAppHelper.launchApp(getContext(), "com.uaes.adviser");
            }
        });
    }

    public void updateHealthyLevel(String healthLevel) {
        String health = "-";
        if (!TextUtils.isEmpty(healthLevel)) {
            health = healthLevel;
        }
        tvDrawerDriveHealthy.setText(health);
    }

    public void updateMaintenanceMileage(int maintenanceMile,float percent) {
        if (maintenanceMile >= 0) {
            tvDrawerDriveDistanceLabel.setText("剩余保养里程");
            tvDrawerDriveDistance.setText(String.valueOf(maintenanceMile));
        } else {
            tvDrawerDriveDistanceLabel.setText("超出保养里程");
            tvDrawerDriveDistance.setText(String.valueOf(Math.abs(maintenanceMile)));
        }
        progressDrawerDriveDistance.setProgress(percent);
    }

    @SuppressLint("SetTextI18n")
    public void updateRank(int rank) {
        tvDrawerDriveRanking.setText(rank +"%");
    }

    public void updateDriveInfo(DriveInfo driveInfo, float maintenancePercent) {
        updateHealthyLevel(driveInfo.getHealthyLevel());
        updateMaintenanceMileage(driveInfo.getMaintenanceMileage(), maintenancePercent);
        updateRank(driveInfo.getRanking());
    }

    public void reset() {

    }

    public Context getContext() {
        return itemView.getContext();
    }
}
