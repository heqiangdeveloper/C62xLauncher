/*
 * 版权：2012-2013 ChinaTsp Co.,Ltd
 × 描述：通用的yes,no,cancel对话框或者信息显示对话框
 * 创建人：liuderu
 * 创建时间：2018-1-16
 */
package com.chinatsp.vehiclesetting.dialogs;

import android.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinatsp.vehiclesetting.R;

public class CommonButtonOrMsgDialog extends BaseDialogFragment {
    private static final String TAG = "CommonButtonOrMsgDialog";
    private View mDlgView;

    private TextView mTvTitle = null;
    private TextView mTvMessage = null;
    private LinearLayout mLayoutButtons = null;
    private Button mBtnYes = null;
    private Button mBtnNo = null;
    private Button mBtnCancel = null;

    private String mTitle = "";
    private String mMessage = "";
    private String mBtnYesText = "";
    private String mBtnNoText = "";
    private String mBtnCancelText = "";
    private View.OnClickListener mOnBtnYesClick = null;
    private View.OnClickListener mOnBtnNoClick = null;
    private View.OnClickListener mOnBtnCancelClick = null;

    private boolean mAutoClose = false;
    private int mAutoCloseInteraval = 3000;


    public static CommonButtonOrMsgDialog newInstance() {
        return new CommonButtonOrMsgDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDlgView = View.inflate(getContext(), R.layout.dialog_common_button_or_msg, null);
        return mDlgView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //getDialog().getWindow().setLayout(1071, 637);
        getDialog().getWindow().setLayout(982, 566);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initEvents();
        show();
    }

    public void setSize(float w, float h) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * w), (int) (dm.heightPixels * h));
    }

    private void initViews() {
        mTvTitle = (TextView) mDlgView.findViewById(R.id.tvDlgTitle);
        mTvMessage = (TextView) mDlgView.findViewById(R.id.tvDlgMsg);
        mLayoutButtons = (LinearLayout) mDlgView.findViewById(R.id.layoutButtons);
        mBtnCancel = (Button) mDlgView.findViewById(R.id.btnDlgCancel);
        mBtnYes = (Button) mDlgView.findViewById(R.id.btnDlgYes);
        mBtnNo = (Button) mDlgView.findViewById(R.id.btnDlgNo);
    }

    private void initEvents() {
        mBtnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnBtnYesClick != null) {
                    mOnBtnYesClick.onClick(v);
                }
                CommonButtonOrMsgDialog.this.dismissAllowingStateLoss();
            }
        });

        mBtnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnBtnNoClick != null) {
                    mOnBtnNoClick.onClick(v);
                }
                CommonButtonOrMsgDialog.this.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnBtnCancelClick != null) {
                    mOnBtnCancelClick.onClick(v);
                }
                CommonButtonOrMsgDialog.this.dismiss();
            }
        });
    }

    public void show() {
        boolean nobutton = true;

        if (mTitle == null || mTitle.equals("")) {
            mTvTitle.setVisibility(View.GONE);
        } else {
            mTvTitle.setText(mTitle);
        }

        if (mBtnCancelText == null || mBtnCancelText.equals("")) {
            mBtnCancel.setVisibility(View.GONE);
        } else {
            mBtnCancel.setText(mBtnCancelText);
            nobutton = false;
        }

        if (mBtnNoText == null || mBtnNoText.equals("")) {
            mBtnNo.setVisibility(View.GONE);
        } else {
            mBtnNo.setText(mBtnNoText);
            nobutton = false;
        }

        if (mBtnYesText == null || mBtnYesText.equals("")) {
            mBtnYes.setVisibility(View.GONE);
        } else {
            mBtnYes.setText(mBtnYesText);
            nobutton = false;
        }

        if (nobutton) {
            mLayoutButtons.setVisibility(View.GONE);
        }

        mTvMessage.setText(mMessage);
        mTvTitle.setText(mTitle);

        if (mAutoClose) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        CommonButtonOrMsgDialog.this.dismissAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, mAutoCloseInteraval);
        }
    }

    public void setTitle(CharSequence title) {
        mTitle = title.toString();
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }


    public View.OnClickListener getOnBtnYesClick() {
        return mOnBtnYesClick;
    }

    public void setYesButton(String btnYesText, View.OnClickListener onBtnYesClick) {
        mBtnYesText = btnYesText;
        mOnBtnYesClick = onBtnYesClick;
    }

    public void setYesButton(View.OnClickListener onBtnYesClick) {
        mOnBtnYesClick = onBtnYesClick;
    }

    public View.OnClickListener getOnBtnNoClick() {
        return mOnBtnNoClick;
    }

    public void setNoButton(String btnNoText, View.OnClickListener onBtnNoClick) {
        mBtnNoText = btnNoText;
        mOnBtnNoClick = onBtnNoClick;
    }

    public View.OnClickListener getOnBtnCancelClick() {
        return mOnBtnCancelClick;
    }

    public void setCancelButton(String btnCancelText, View.OnClickListener onBtnCancelClick) {
        mBtnCancelText = btnCancelText;
        mOnBtnCancelClick = onBtnCancelClick;
    }

    public boolean isAutoClose() {
        return mAutoClose;
    }

    public void setAutoClose(boolean autoClose) {
        mAutoClose = autoClose;
    }

    public void setAutoClose(boolean autoClose, int interval) {
        mAutoClose = autoClose;
        mAutoCloseInteraval = interval;
    }

    public int getAutoCloseInteraval() {
        return mAutoCloseInteraval;
    }

    public void setAutoCloseInteraval(int autoCloseInteraval) {
        mAutoCloseInteraval = autoCloseInteraval;
    }
}
