package com.chinatsp.driveinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import launcher.base.utils.EasyLog;


public class DriveCounselorCardView extends ConstraintLayout {

    private final String TAG = "DriveCounselorCardView";

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


    private void init(){
        EasyLog.i(TAG,"init , hashcode:"+this.hashCode());
        LayoutInflater.from(getContext()).inflate(R.layout.card_drive_counselor, this);
        mViewHolder = new ViewHolder(this);
        mController = new DriveCounselorController(this);
        initViews();
    }

    private void initViews() {
        View ivCardDriveReturn = findViewById(R.id.ivCardDriveReturn);
        ivCardDriveReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.readInfo();
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        EasyLog.d(TAG, "onVisibilityChanged : "+visibility);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EasyLog.d(TAG, "onAttachedToWindow");
        mController.attachListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EasyLog.d(TAG, "onDetachedFromWindow");
        mController.detachListener();
    }

    private static class ViewHolder{
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
            tvCardDriveOilValue.setText(String.valueOf(driveInfo.getOilConsumption()));
            tvCardDriveTimeValue.setText(String.valueOf(driveInfo.getDrivingTime()));
            tvCardDriveDistanceValue.setText(String.valueOf(driveInfo.getDrivingMileage()));
        }

        private void updateOilConsumption(float oil) {
            tvCardDriveOilValue.setText(String.valueOf(oil));
        }
        private void updateDriveTime(int time) {
            tvCardDriveTimeValue.setText(String.valueOf(time));
        }
        private void updateDriveMileage(float distance) {
            tvCardDriveDistanceValue.setText(String.valueOf(distance));
        }
    }

    void updateDriveInfo(DriveInfo driveInfo) {
        post(() -> mViewHolder.update(driveInfo));
    }

    void updateOilConsumption(float oil) {
        post(() -> mViewHolder.updateOilConsumption(oil));
    }

    void updateDriveTime(int time) {
        post(() -> mViewHolder.updateDriveTime(time));
    }

    void updateDriveMileage(float distance) {
        post(() -> mViewHolder.updateDriveMileage(distance));
    }
}
