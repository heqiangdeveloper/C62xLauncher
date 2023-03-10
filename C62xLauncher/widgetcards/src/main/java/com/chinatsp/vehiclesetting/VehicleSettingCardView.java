package com.chinatsp.vehiclesetting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.widgetcards.R;

import launcher.base.service.AppServiceManager;
import launcher.base.service.car.ICarService;
import launcher.base.service.car.OffLineConfig;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;


public class VehicleSettingCardView extends ConstraintLayout {

    public VehicleSettingCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public VehicleSettingCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VehicleSettingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VehicleSettingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private View ivOpenWindow, ivCloseWindow;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.card_vehicle_setting, this);
        ivOpenWindow = findViewById(R.id.ivVehicleSettingOpenWindow);
        ivCloseWindow = findViewById(R.id.ivVehicleSettingCloseWindow);
        ivOpenWindow.setOnClickListener(mOnClickListener);
        ivCloseWindow.setOnClickListener(mOnClickListener);
        initButtonStatus();
    }

    private void initButtonStatus() {
        ICarService carService = (ICarService)AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        int offLineCfg = carService.getOffLineCfg();
        EasyLog.i("VehicleSettingCardView", "initButtonStatus , offLineCfg: "+offLineCfg);
        if (offLineCfg <= OffLineConfig.LV3) {
            ivOpenWindow.setVisibility(GONE);
            ivCloseWindow.setVisibility(GONE);
        } else {
            ivOpenWindow.setVisibility(VISIBLE);
            ivCloseWindow.setVisibility(VISIBLE);
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == ivOpenWindow) {
                openWindow();
            } else if (v == ivCloseWindow) {
                closeWindow();
            }
        }

    };

    private void closeWindow() {
        ICarService carService = (ICarService)AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        carService.doSwitchWindow(true);
    }

    private void openWindow() {
        ICarService carService = (ICarService)AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
        carService.doSwitchWindow(false);
    }
}
