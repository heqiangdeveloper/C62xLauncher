package com.common.library.frame.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;

import androidx.annotation.NonNull;

import com.common.library.frame.R;


public class BaseProgressDialog extends Dialog {

    public static BaseProgressDialog newInstance(Context context) {
        return new BaseProgressDialog(context);
    }

    public BaseProgressDialog(@NonNull Context context) {
        this(context, R.style._progress_dialog);
    }

    public BaseProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initUI();
    }

    public BaseProgressDialog(@NonNull Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initUI();
    }

    private void initUI() {
        getWindow().getAttributes().gravity = Gravity.CENTER;
        setCanceledOnTouchOutside(false);
    }

}