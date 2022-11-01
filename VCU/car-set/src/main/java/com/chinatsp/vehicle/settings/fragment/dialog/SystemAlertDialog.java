package com.chinatsp.vehicle.settings.fragment.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.chinatsp.vehicle.settings.R;

public class SystemAlertDialog extends AlertDialog {
    private View mLayoutView;
    private TextView detailsContent, hintConform;
    private boolean cancelable;

    protected SystemAlertDialog(@NonNull Context context) {
        super(context);
        mLayoutView = View.inflate(getContext(), R.layout.global_dialog_fragment, null);
        findViews(mLayoutView);
    }

    protected SystemAlertDialog(@NonNull Context context, int themeResId) {
        super(context);
        mLayoutView = View.inflate(getContext(), themeResId, null);
        findViews(mLayoutView);
    }

    private void findViews(View layoutView) {
        detailsContent = layoutView.findViewById(R.id.details_content);
        hintConform = layoutView.findViewById(R.id.hint_conform);
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


    public void setDetailsContent(int stringId) {
        if (detailsContent != null) {
            detailsContent.setText(stringId);
        }
    }
    public void setIsConform(boolean cancelable) {
        this.cancelable = cancelable;
        if(cancelable){
            hintConform.setVisibility(View.VISIBLE);
        }else{
            hintConform.setVisibility(View.INVISIBLE);
        }

    }
}
