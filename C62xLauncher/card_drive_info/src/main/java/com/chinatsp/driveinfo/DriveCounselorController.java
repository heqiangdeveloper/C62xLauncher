package com.chinatsp.driveinfo;

import com.chinatsp.driveinfo.callback.IReadDriveInfoListener;
import com.cihon.client.SmallCardCallback;

import launcher.base.utils.EasyLog;

public class DriveCounselorController {
    private final String TAG = "DriveCounselorController";
    private DriveCounselorCardView mView;
    private final DriveInfoRepository mRepository;

    public DriveCounselorController(DriveCounselorCardView view) {
        mView = view;
        mRepository = DriveInfoRepository.getInstance();
        mRepository.setReadDriveInfoListener(mReadDriveInfoListener);
        mRepository.bindService(mView.getContext().getApplicationContext());
    }

    private final IReadDriveInfoListener mReadDriveInfoListener = new IReadDriveInfoListener() {
        @Override
        public void onSuccess(DriveInfo driveInfo) {
            EasyLog.i(TAG, "IReadDriveInfoListener  onSuccess: " + driveInfo);
            EasyLog.i(TAG, "IReadDriveInfoListener  onSuccess, thread " + Thread.currentThread().getName());
            if (mView != null) {
                mView.updateDriveInfo(driveInfo);
            }
        }
    };
    private final SmallCardCallback mSmallCardCallback = new SmallCardCallback() {
        @Override
        public void onOilChanged(float oilConsumption) {
            EasyLog.i(TAG, "SmallCardCallback  OilChanged: " + oilConsumption+", thread:"+Thread.currentThread().getName());
            if (mView != null) {
                mView.updateOilConsumption(oilConsumption);
            }
        }

        @Override
        public void onDrivingTimeChanged(int driveTime) {
            EasyLog.i(TAG, "SmallCardCallback  DrivingTimeChanged: " + driveTime+", thread:"+Thread.currentThread().getName());
            if (mView != null) {
                mView.updateDriveTime(driveTime);
            }
        }

        @Override
        public void onDrivingMileageChanged(float driveMileage) {
            EasyLog.i(TAG, "SmallCardCallback  DrivingMileageChanged: " + driveMileage+", thread:"+Thread.currentThread().getName());
            if (mView != null) {
                mView.updateDriveMileage(driveMileage);
            }
        }
    };

    public void readInfo() {
        DriveInfo driveInfo = mRepository.readDriveInfo();
        mView.updateDriveInfo(driveInfo);
    }

    void attachListener() {
        mRepository.setDriveInfoCallback(mSmallCardCallback);
        mRepository.setReadDriveInfoListener(mReadDriveInfoListener);
    }

    void detachListener() {
        mRepository.setDriveInfoCallback(null);
        mRepository.setReadDriveInfoListener(null);
    }
}
