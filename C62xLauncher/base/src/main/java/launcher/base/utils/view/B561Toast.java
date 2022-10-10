package launcher.base.utils.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import launcher.base.R;


public class B561Toast {
//    private static View toastView;
//    private static TextView toastTv;
//    private static Toast mToast;

    private static final int SHOW_SHORT = 2000;
    private static final int SHOW_LONG = 3000;

    public static void showShort(Context context, @StringRes int resId) {
        String message = context.getString(resId);
        show(context, message, SHOW_SHORT);
    }

    public static void showShort(Context context, String message) {
        show(context, message, SHOW_SHORT);
    }

    public static void show(Context context, @StringRes int resId, int time) {
        String message = context.getString(resId);
        show(context, message, time);
    }

    private static long lastToastTime;
    private static final int MIN_INTERVAL_TIME = 3000;

    public static void show(Context context, String message, int time) {
        long now = System.currentTimeMillis();
        long interval = now - lastToastTime;
        if (interval < MIN_INTERVAL_TIME) {
            return;
        }
        lastToastTime = now;
        Toast mToast = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        View toastView = inflater.inflate(R.layout.toast_common, null, false);
        toastView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,90));
        TextView toastTv = toastView.findViewById(R.id.toast_message);

        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(toastView);
        mToast.setDuration(time);
        toastTv.setText(message);
        mToast.show();
    }
}
