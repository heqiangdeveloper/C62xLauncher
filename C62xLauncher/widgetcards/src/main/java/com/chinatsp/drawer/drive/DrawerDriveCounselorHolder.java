package com.chinatsp.drawer.drive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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

    private final ImageView ivDrawerDriveHealthyText;
    private final TextView tvDrawerDriveDistance;
    private final TextView tvDrawerDriveDistanceLabel;
    private final TextView tvDrawerDriveRanking;
    private ImageView ivDrawerDriveHealthBottom;
    private final DistanceCircleProgress progressDrawerDriveDistance;


    public DrawerDriveCounselorHolder(@NonNull View itemView) {
        super(itemView);
        EasyLog.d("DrawerDriveCounselorHolder", "init " + hashCode());
        mController = new DriveInfoDrawerController(this);
        ivDrawerDriveHealthyText = itemView.findViewById(R.id.ivDrawerDriveHealthyText);
        ivDrawerDriveHealthBottom = itemView.findViewById(R.id.ivDrawerDriveHealthBottom);
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
        setTextLineGradient(tvDrawerDriveDistance);
        setTextLineGradient(tvDrawerDriveDistanceLabel);
        setTextLineGradient(tvDrawerDriveRanking);
    }

    int colorEnd = Color.parseColor("#46FCFF");
    int  colorStart = Color.parseColor("#7D85DE");

    private void setTextLineGradient(TextView textView) {
        if (textView == null) {
            return;
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                Shader shader = new LinearGradient(0, 0, textView.getWidth(), 0, colorStart, colorEnd, Shader.TileMode.CLAMP);
                textView.getPaint().setShader(shader);
                textView.invalidate();
            }
        });
    }

    public void updateHealthyLevel(String healthLevel) {
        String health = "-";
        if (!TextUtils.isEmpty(healthLevel)) {
            health = healthLevel;
        }
        HealthRes healthRes = HealthRes.getHealthRes(healthLevel, getContext());
        ivDrawerDriveHealthyText.setImageResource(healthRes.textDrawableId);
        ivDrawerDriveHealthBottom.setImageResource(healthRes.bottomDrawableId);
    }

    public void updateMaintenanceMileage(int maintenanceMile, float percent) {
        if (maintenanceMile >= 0) {
            tvDrawerDriveDistanceLabel.setText(R.string.drawer_consultant_maintenance_mileage);
            tvDrawerDriveDistance.setText(String.valueOf(maintenanceMile));
        } else {
            tvDrawerDriveDistanceLabel.setText(R.string.drawer_consultant_service_mileage_exceed);
            tvDrawerDriveDistance.setText(String.valueOf(Math.abs(maintenanceMile)));
        }
        progressDrawerDriveDistance.setProgress(percent);
    }

    @SuppressLint("SetTextI18n")
    public void updateRank(int rank) {
        tvDrawerDriveRanking.setText(rank + "%");
        setTextLineGradient(tvDrawerDriveRanking);
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

    @Override
    public void bind(int position, DrawerEntity drawerEntity) {
        super.bind(position, drawerEntity);
        EasyLog.d("DrawerDriveCounselorHolder", "bind " + hashCode());
    }
}
