package com.chinatsp.drawer.search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.chinatsp.widgetcards.R;

import launcher.base.utils.EasyLog;


public class LauncherSearchActivity extends AppCompatActivity {
    private final String TAG = "LauncherSearchActivity";

    private EditText mEdittextSearchWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEdittextSearchWord = findViewById(R.id.edittextSearchWord);
        mEdittextSearchWord.addTextChangedListener(mTextWatcher);
    }

    public void clickBackBtn(View view) {
        finish();
    }

    public void clearSearchText(View view) {

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            EasyLog.d(TAG,"word length:"+s.toString().length());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEdittextSearchWord.removeTextChangedListener(mTextWatcher);
    }
}