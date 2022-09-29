package com.chinatsp.drawer.drive;

import android.os.Handler;

import com.chinatsp.driveinfo.DriveInfo;
import com.chinatsp.driveinfo.DriveInfoRepository;
import com.chinatsp.driveinfo.callback.ILauncherWidgetCallback;
import com.chinatsp.driveinfo.callback.IReadDriveInfoListener;

import launcher.base.utils.EasyLog;

class DriveInfoDrawerController {
    private final String TAG = "DriveInfoDrawerController";
    private DrawerDriveCounselorHolder mViewHolder;
    private DriveInfoRepository mDriveInfoRepository;
    private DriveInfo mDriveInfo;
    private final int MIN_MAINTAIN = -10239;
    private final int MAX_MAINTAIN = 5000;
    private Handler mMainHandler = new android.os.Handler();


    DriveInfoDrawerController(DrawerDriveCounselorHolder viewHolder) {
        EasyLog.d(TAG, "DriveInfoDrawerController init" );
        mViewHolder = viewHolder;
        mDriveInfoRepository = DriveInfoRepository.getInstance();
        mDriveInfoRepository.setDrawerReadDriveInfoListener(mReadDriveInfoListener);
        mDriveInfoRepository.setWidgetCallback(mILauncherWidgetCallback);
        if (!mDriveInfoRepository.isServiceConnect()) {
            mDriveInfoRepository.bindServiceAsync(viewHolder.getContext().getApplicationContext());
        }
    }

    private IReadDriveInfoListener mReadDriveInfoListener = new IReadDriveInfoListener() {
        @Override
        public void onSuccess(DriveInfo driveInfo) {
            EasyLog.d(TAG, "IReadDriveInfoListener read onSuccess: " + driveInfo);
            mDriveInfo = driveInfo;
            mMainHandler.post(() -> updateViewHolder(mDriveInfo));
        }
    };


    private ILauncherWidgetCallback mILauncherWidgetCallback = new ILauncherWidgetCallback() {
        @Override
        public void onHealthyLevelChanged(String healthLevel) {
            EasyLog.d(TAG, "ILauncherWidgetCallback onHealthyLevelChanged: " + healthLevel);
            if (mViewHolder != null) {
                mMainHandler.post(() -> mViewHolder.updateHealthyLevel(healthLevel));
            }
        }

        @Override
        public void onMaintenanceMileageChanged(int maintenanceMile) {
            EasyLog.d(TAG, "ILauncherWidgetCallback onMaintenanceMileageChanged: " + maintenanceMile);
            if (mViewHolder != null) {
                float percent = computeDistancePercent(maintenanceMile);
                mMainHandler.post(() -> mViewHolder.updateMaintenanceMileage(maintenanceMile, percent));
            }
        }

        @Override
        public void onRankingChanged(int rank) {
            EasyLog.d(TAG, "ILauncherWidgetCallback onRankingChanged: " + rank);
            if (mViewHolder != null) {
                mMainHandler.post(() -> mViewHolder.updateRank(rank));
            }
        }
    };

    private float computeDistancePercent(float maintenanceMile) {
        if (maintenanceMile < 0) {
            return 0f;
        }
        return maintenanceMile / MAX_MAINTAIN;
    }


    public void readDriveInfo() {
        EasyLog.d(TAG, "readDriveInfo" );
        DriveInfo driveInfo = mDriveInfoRepository.readDriveInfo();
        mMainHandler.post(() -> updateViewHolder(driveInfo));
    }
    private void updateViewHolder(DriveInfo driveInfo) {
        if (mViewHolder != null) {
            if (driveInfo == null) {
                mViewHolder.reset();
            } else {
                mViewHolder.updateDriveInfo(driveInfo, computeDistancePercent(driveInfo.getMaintenanceMileage()));
            }
        }
    }
}
