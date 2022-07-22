package card.views.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import card.service.R;
import launcher.base.service.theme.IThemeService;


public class CustomDialog extends AlertDialog {
    private View mLayoutView;
    private ImageView ivDialogIcon;
    private TextView mMessageView;
    private TextView mBtnOk;
    private TextView mBtnCancel;
    private IThemeService mShowMode ;

    protected CustomDialog(@NonNull Context context) {
        super(context);
        mLayoutView = View.inflate(getContext(), R.layout.dialog_common, null);
        findViews(mLayoutView);
    }

    protected CustomDialog(@NonNull Context context, int layoutResId) {
        super(context);
        mLayoutView = View.inflate(getContext(), layoutResId, null);
        findViews(mLayoutView);
    }

    private void findViews(View layoutView) {
        ivDialogIcon = layoutView.findViewById(R.id.ivDialogIcon);
        mMessageView = layoutView.findViewById(R.id.tvDialogMessage);
        mBtnOk = layoutView.findViewById(R.id.btnDialogOk);
        mBtnCancel = layoutView.findViewById(R.id.btnDialogCancel);
    }

    public void setLayout(int width, int height) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER;
        mLayoutView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutView);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mLayoutView.findViewById(id);
    }


    public void setMessage(int stringId) {
        if (mMessageView != null) {
            mMessageView.setText(stringId);
        }
    }

    public void setBtnOkText(int stringId) {
        if (mBtnOk != null) {
            mBtnOk.setText(stringId);
        }
    }

    public void setTitleIcon(int iconResId) {
        if (ivDialogIcon != null) {
            ivDialogIcon.setImageResource(iconResId);
        }
    }
    public void setTextColor(TextView textView, int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }
}
