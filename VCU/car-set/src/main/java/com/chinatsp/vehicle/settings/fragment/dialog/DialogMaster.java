package com.chinatsp.vehicle.settings.fragment.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chinatsp.settinglib.manager.Hint;
import com.chinatsp.vehicle.settings.R;

public class DialogMaster {
    private SystemAlertDialog dialog;

    public static DialogMaster create(Context activity, OnPressOk onPressOk, OnPressCancel onPressCancel, int width, int height,int signal) {
        return create(activity, onPressOk, onPressCancel, R.layout.global_dialog_fragment, width, height,signal);
    }

    public static DialogMaster create(Context activity, OnPressOk onPressOk, OnPressCancel onPressCancel, int layoutResId,
                                      int width, int height ,int signal) {

        DialogMaster dialogMaster = new DialogMaster();
        if(signal== Hint.wirelessChargingAbnormal){
            /**充电异常*/
            dialogMaster.dialog = new SystemAlertDialog(activity, R.layout.abnormal_charge_dialog_fragment);
        }else if(signal== Hint.wirelessChargingMetal){
            /**检测到金属异物，请移开异物*/
            dialogMaster.dialog = new SystemAlertDialog(activity, R.layout.foreign_matter_dialog_fragment);
        }else if(signal== Hint.wirelessChargingTemperature){
            /**无线充电温度过高，请移开手机*/
            dialogMaster.dialog = new SystemAlertDialog(activity, R.layout.temperature_dialog_fragment);
        }else{
            dialogMaster.dialog = new SystemAlertDialog(activity, R.layout.global_dialog_fragment);
        }

        dialogMaster.dialog.setLayout(width, height);
        dialogMaster.insertListener(onPressOk, onPressCancel);
        // 设置背景透明
        Window window = dialogMaster.dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
        return dialogMaster;
    }

    public SystemAlertDialog getDialog() {
        return dialog;
    }

    public void setLocation(int y) {
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.y = y;
        }
    }

    private void insertListener(OnPressOk onPressOk, OnPressCancel onPressCancel) {
        View okBtn = dialog.findViewById(R.id.hint_conform);
        //View okCancel = dialog.findViewById(R.id.btnDialogCancel);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == okBtn) {
                    if (onPressOk != null) {
                        onPressOk.onPress(v);
                    }
                } /*else if (v == okCancel) {
                    if (onPressCancel != null) {
                        onPressCancel.onPress(v);
                    }
                }*/
                dialog.dismiss();
            }
        };
        if (okBtn != null) {
            okBtn.setOnClickListener(onClickListener);
        }
        /*if (okCancel != null) {
            okCancel.setOnClickListener(onClickListener);
        }*/
    }

    public interface OnPressOk {
        void onPress(View v);
    }

    public interface OnPressCancel {
        void onPress(View v);
    }

}
