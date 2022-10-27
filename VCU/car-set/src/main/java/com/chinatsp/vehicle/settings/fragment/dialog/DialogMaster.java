package com.chinatsp.vehicle.settings.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chinatsp.vehicle.settings.R;

public class DialogMaster {
    private SystemAlertDialog dialog;

    public static DialogMaster create(Context activity, OnPressOk onPressOk, OnPressCancel onPressCancel,int width,int height) {
        return create(activity, onPressOk, onPressCancel, R.layout.global_dialog_fragment, width, height);
    }
    public static DialogMaster create(Context activity, OnPressOk onPressOk, OnPressCancel onPressCancel, int layoutResId,
                                      int width, int height) {
        DialogMaster dialogMaster = new DialogMaster();
        dialogMaster.dialog = new SystemAlertDialog(activity,layoutResId);
        dialogMaster.dialog.setLayout(width, height);
        dialogMaster.insertListener(onPressOk,onPressCancel);
        // 设置背景透明
        Window window = dialogMaster.dialog.getWindow();
        //window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
        return dialogMaster;
    }
    public SystemAlertDialog getDialog(){
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

    public interface OnPressOk{
        void onPress(View v);
    }
    public interface OnPressCancel{
        void onPress(View v);
    }

}
