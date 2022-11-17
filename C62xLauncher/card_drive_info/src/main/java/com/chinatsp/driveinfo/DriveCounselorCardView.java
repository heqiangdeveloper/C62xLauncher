package com.chinatsp.driveinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;


public class DriveCounselorCardView extends ConstraintLayout {

    private static final String TAG = "DriveCounselorCardView";

    public DriveCounselorCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public DriveCounselorCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DriveCounselorCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DriveCounselorCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private ViewHolder mViewHolder;
    private DriveCounselorController mController;


    private void init() {
        EasyLog.i(TAG, "init , hashcode:" + this.hashCode());
//        printStack();
        LayoutInflater.from(getContext()).inflate(R.layout.card_drive_counselor, this);
        mViewHolder = new ViewHolder(this);
        mController = new DriveCounselorController(this);
        initViews();
    }

    private void printStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            EasyLog.i(TAG, "printStack: "+stackTrace[i]);
        }
    }

    private void initViews() {
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RecentAppHelper.launchApp(getContext(), "com.uaes.adviser");
//            }
//        });

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        EasyLog.d(TAG, "onVisibilityChanged : " + visibility);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EasyLog.d(TAG, "DriveInfoXXX onAttachedToWindow: "+hashCode());
        mController.attachListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EasyLog.d(TAG, "DriveInfoXXX onDetachedFromWindow: "+hashCode());
        mController.detachListener();
    }

    private static class ViewHolder {
        private final TextView tvCardDriveOilValue;
        private final TextView tvCardDriveTimeValue;
        private final TextView tvCardDriveDistanceValue;

        public ViewHolder(View rootView) {
            tvCardDriveOilValue = rootView.findViewById(R.id.tvCardDriveOilValue);
            tvCardDriveTimeValue = rootView.findViewById(R.id.tvCardDriveTimeValue);
            tvCardDriveDistanceValue = rootView.findViewById(R.id.tvCardDriveDistanceValue);
        }

        private void update(DriveInfo driveInfo) {
            if (driveInfo == null) {
                return;
            }
            updateOilConsumption(driveInfo.getOilConsumption());
            tvCardDriveTimeValue.setText(String.valueOf(driveInfo.getDrivingTime()));
            updateDriveMileage(driveInfo.getDrivingMileage());
        }

        private void updateOilConsumption(float oil) {
            if (Math.abs(oil) < 0.01) {
                tvCardDriveOilValue.setText(String.valueOf(0));
            } else {
                tvCardDriveOilValue.setText(String.valueOf(oil));
            }
        }

        private void updateDriveTime(int time) {
            EasyLog.d(TAG, "ViewHolder updateDriveTime");
            tvCardDriveTimeValue.setText(String.valueOf(time));
        }

        private void updateDriveMileage(float distance) {
            if (Math.abs(distance) < 0.01) {
                tvCardDriveDistanceValue.setText(String.valueOf(0));
            } else {
                tvCardDriveDistanceValue.setText(String.valueOf(distance));
            }
        }
    }

    void updateDriveInfo(DriveInfo driveInfo) {
        post(() -> mViewHolder.update(driveInfo));
    }

    void updateOilConsumption(float oil) {
        post(() -> mViewHolder.updateOilConsumption(oil));
    }

    void updateDriveTime(int time) {
        EasyLog.d(TAG, "updateDriveTime: card hashcode:" + hashCode());
        post(() -> mViewHolder.updateDriveTime(time));
    }

    void updateDriveMileage(float distance) {
        post(() -> mViewHolder.updateDriveMileage(distance));
    }
}
