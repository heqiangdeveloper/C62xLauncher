package com.chinatsp.vehiclesetting.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinatsp.vehiclesetting.R;
import com.chinatsp.vehiclesetting.utils.HandlerUtils;

public class InputDialog extends BaseDialogFragment {
    private View mDlgView;

    private Button buttonCancel, buttonConfirm;
    private EditText editTextInput;
    private ImageView dialog_input_delate;
    InputMethodManager imm;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDlgView = inflater.inflate(R.layout.dialog_input, container);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (editTextInput != null && editTextInput.requestFocus()) {
                    imm = (InputMethodManager) getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    HandlerUtils.get().post(new Runnable() {
                        @Override
                        public void run() {
                            if (imm != null) {
                                imm.showSoftInput(editTextInput, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    });
                }
            }
        });
        return mDlgView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initEvents();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.9), dm.heightPixels);
    }

    @Override
    public void onStop() {
        super.onStop();
        //LogUtils.d("fragment onStop");
        //imm.hideSoftInputFromWindow(editTextInput.getWindowToken(),0);
        HandlerUtils.get().post(new Runnable() {
            @Override
            public void run() {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editTextInput.getWindowToken(),0);
                }
                //dismissAllowingStateLoss();//
            }
        });
    }

    private void initViews() {
        buttonCancel = (Button) mDlgView.findViewById(R.id.buttonCancel);
        buttonConfirm = (Button) mDlgView.findViewById(R.id.buttonConfirm);
        editTextInput = (EditText) mDlgView.findViewById(R.id.editTextInput);
        dialog_input_delate = (ImageView) mDlgView.findViewById(R.id.dialog_input_delate);

        editTextInput.setText(orgText);
        editTextInput.setSelection(orgText.length());
    }

    private int nInputMaxLength = 32;

    public void setInputMaxLength(int maxLength) {
        nInputMaxLength = maxLength;
    }

    private void initEvents() {
        editTextInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(nInputMaxLength)
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputListener != null)
                    inputListener.cancel();
                dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputListener != null) {
                    String text = editTextInput.getText().toString().trim();
                    if (orgText.equals(text)) {
                    } else {
                        inputListener.ok(text);
                    }
                }
                dismiss();
            }
        });
        dialog_input_delate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextInput.setText("");
            }
        });
        if (type == INPUT_TYPE_PWD) {
            buttonConfirm.setEnabled(editTextInput.getText().length() >= 8);
            editTextInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    buttonConfirm.setEnabled(s.length() >= 8);
                }
            });
        }
    }

    private String orgText = "";
    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_PWD = 1;
    private int type = INPUT_TYPE_TEXT;//0:text;1 :pwd

    public void setEditText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        orgText = text;
    }

    public void setEditTextInputType(int type) {
        this.type = type;
    }

    IInputDialogListener inputListener;

    public void setOnInputListener(IInputDialogListener inputListener) {
        this.inputListener = inputListener;
    }

    public interface IInputDialogListener {
        void cancel();

        void ok(String result);
    }

}
