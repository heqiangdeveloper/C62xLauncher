package com.chinatsp.vehiclesetting.dialogs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chinatsp.vehiclesetting.R;

public class BaseDialogFragment extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.tdialog_net_theme);
        super.setCancelable(mCancelable);
    }

    private boolean mCancelable = true;

    public void setCancelable(boolean mCancelable){
        this.mCancelable = mCancelable;
    }

    @Override
    public void onStart() {
        //WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        //layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        super.onStart();
    }

    private String tag;

    @Override
    public void show(FragmentManager manager, String tag) {
        this.tag = tag;
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null || !fragment.isAdded()) {
            try{
                super.show(manager, tag);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
