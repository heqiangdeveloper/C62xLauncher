package com.chinatsp.drawer.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.chinatsp.drawer.adapter.SearchAdapter;
import com.chinatsp.drawer.bean.SearchBean;
import com.chinatsp.drawer.search.db.SearchDB;
import com.chinatsp.drawer.search.utils.FileUtils;
import com.chinatsp.widgetcards.R;

import java.util.List;

import launcher.base.utils.EasyLog;


public class LauncherSearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListerner {
    private final String TAG = "LauncherSearchActivity";

    private EditText mEdittextSearchWord;
    private SearchDB db;
    private RecyclerView rcvSearch;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = new SearchDB(this);
        mEdittextSearchWord = findViewById(R.id.edittextSearchWord);
        mEdittextSearchWord.addTextChangedListener(mTextWatcher);
        rcvSearch = findViewById(R.id.rcvSearch);
        rcvSearch.setLayoutManager(new LinearLayoutManager(
                LauncherSearchActivity.this,
                LinearLayoutManager.VERTICAL, false));
    }

    public void clickBackBtn(View view) {
        finish();
    }

    public void clearSearchText(View view) {
        //mEdittextSearchWord.setText(mEdittextSearchWord.getText().toString().charAt(mEdittextSearchWord.getText().length()-1));
        int index = mEdittextSearchWord.getSelectionStart();   //获取Edittext光标所在位置
        String str = mEdittextSearchWord.getText().toString();
        if (!str.equals("")) {//判断输入框不为空，执行删除
            mEdittextSearchWord.getText().delete(index - 1, index);
        }
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
            EasyLog.d(TAG, "word length:" + s.toString().length());
            if (s.toString().length() > 0) {
                findViewById(R.id.search_hint).setVisibility(View.GONE);
                //List<SearchBean> beans = FileUtils.fuzzySearch(s.toString(),db.getData());
                List<SearchBean> beans = db.getData1(s.toString());
                if (beans.size() == 0) {
                    findViewById(R.id.rcvSearch).setVisibility(View.GONE);
                    findViewById(R.id.list_hint).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.rcvSearch).setVisibility(View.VISIBLE);
                    findViewById(R.id.list_hint).setVisibility(View.GONE);
                    adapter = new SearchAdapter(LauncherSearchActivity.this, beans,s.toString());
                    adapter.setOnItemClickListerner(LauncherSearchActivity.this);
                    rcvSearch.setAdapter(adapter);
                }
            } else {
                findViewById(R.id.search_hint).setVisibility(View.VISIBLE);
                findViewById(R.id.rcvSearch).setVisibility(View.GONE);
                findViewById(R.id.list_hint).setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEdittextSearchWord.removeTextChangedListener(mTextWatcher);
    }

    @Override
    public void onItemClick(int position, SearchBean bean) {
        findViewById(R.id.search_hint).setVisibility(View.VISIBLE);
        findViewById(R.id.rcvSearch).setVisibility(View.GONE);
        findViewById(R.id.list_hint).setVisibility(View.GONE);
        mEdittextSearchWord.setText("");
        if(TextUtils.isEmpty(bean.getIntentInterface())){
            //打开应用
            FileUtils.launchApp(this,bean.getIntentAction());
        }else {
            //打开某个应用某个模块
            Intent intent = new Intent(bean.getIntentAction());
            intent.putExtra("type", bean.getIntentInterface());
            startActivity(intent);
        }
    }
}