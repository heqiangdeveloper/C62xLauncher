package com.chinatsp.drawer.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.drawer.adapter.SearchAdapter;
import com.chinatsp.drawer.bean.SearchBean;
import com.chinatsp.drawer.bean.SearchHistoricalBean;
import com.chinatsp.drawer.search.db.SearchDB;
import com.chinatsp.drawer.search.manager.SearchManager;
import com.chinatsp.drawer.search.utils.FileUtils;
import com.chinatsp.drawer.search.utils.Flowlayout;
import com.chinatsp.widgetcards.R;

import java.util.List;

import launcher.base.utils.EasyLog;


public class LauncherSearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListerner {
    private final String TAG = "LauncherSearchActivity";
    private Flowlayout tagLayout;
    private EditText mEdittextSearchWord;
    private SearchDB db;
    private RecyclerView rcvSearch;
    private SearchAdapter adapter;

    // 存放标签数据的数组
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
        tagLayout = findViewById(R.id.tagLayout);
        initLayout();
    }

    public void clickBackBtn(View view) {
        finish();
    }

    public void clearSearchText(View view) {
        //mEdittextSearchWord.setText(mEdittextSearchWord.getText().toString().charAt(mEdittextSearchWord.getText().length()-1));
        FileUtils.hideSoftInput(this);
        int index = mEdittextSearchWord.getSelectionStart();   //获取Edittext光标所在位置
        if (index <= 0) {
            return;
        }
        String str = mEdittextSearchWord.getText().toString();
        if (!str.equals("")) {//判断输入框不为空，执行删除
            //mEdittextSearchWord.getText().delete(index - 1, index);
            mEdittextSearchWord.setText(null);
        }
    }

    public void SearchText(View view) {
        //mEdittextSearchWord.setText(mEdittextSearchWord.getText().toString().charAt(mEdittextSearchWord.getText().length()-1));
        String str = mEdittextSearchWord.getText().toString();
        if (!str.equals("")) {//判断输入框不为空，执行搜索
            searchEdit(str);
        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            EasyLog.d(TAG, "word length:" + s.toString().length());
            searchEdit(s.toString());
        }
    };

    public void clearHistorical(View view) {
        db.deleteHistorical();
        initLayout();
    }

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
        List<SearchHistoricalBean> list = db.getHistoricalData();
        if (list.size() == 0) {
            db.insertSearchHistorical(mEdittextSearchWord.getText().toString());
            initLayout();
        } else {
            boolean historicalIdentical = false;
            for (int i = 0; i < list.size(); i++) {
                //判断是否数据库有相同内容，如果有就不插入数据库
                if (list.get(i).getContent().equals(mEdittextSearchWord.getText().toString())) {
                    historicalIdentical = true;
                    break;
                }
            }
            if (!historicalIdentical) {
                if (list.size() == 10) {
                    db.deleteLocation1();
                }
                //插入搜索历史记录
                db.insertSearchHistorical(mEdittextSearchWord.getText().toString());
                initLayout();
            }
        }
        if (TextUtils.isEmpty(bean.getIntentInterface())) {
            if (bean.getIntentAction().equals("com.chinatsp.appmanagement")) {
                Intent intent = new Intent();
                intent.putExtra("operation", 1);
                intent.setAction("com.chinatsp.launcher.appmanegement");
                sendBroadcast(intent);
            } else {
                //打开应用
                FileUtils.launchApp(this, bean.getIntentAction());
            }
        } else {
            //打开某个应用某个模块
            try {
                String intentAction = bean.getIntentAction();
                EasyLog.i("XXXXXX", "intentAction:" + intentAction);
                Intent intent = new Intent(intentAction);
                intent.putExtra("type", bean.getIntentInterface());
                intent.putExtra("INTENT_PATH", "LAUNCHER_SEARCH");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mEdittextSearchWord.setText("");
    }

    private void initLayout() {
        List<SearchHistoricalBean> list = db.getHistoricalData();
        tagLayout.removeAllViewsInLayout();
        if (list.size() == 0) {
            findViewById(R.id.historical_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.historical_layout).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < list.size(); i++) {
            final View view = LayoutInflater.from(LauncherSearchActivity.this).inflate(R.layout.search_historical, tagLayout, false);
            final TextView text = view.findViewById(R.id.text);  //查找  到当前     textView
            final ImageView icon = view.findViewById(R.id.delete_icon);  //查找  到当前  删除小图标
            text.setText(list.get(i).getContent());
            int finalI = i;
            text.setOnClickListener(v -> {
                mEdittextSearchWord.setText(list.get(finalI).getContent());
                mEdittextSearchWord.setSelection(list.get(finalI).getContent().length());//将光标移到文字最后
            });
            text.setOnLongClickListener(v -> {
                icon.setVisibility(View.VISIBLE);
                text.setBackground(ContextCompat.getDrawable(LauncherSearchActivity.this, R.drawable.soushuo_sel_bg_198));
                return true;
            });
            icon.setOnClickListener(v -> {
                db.deleteCountHistorical(list.get(finalI).getContent());
                initLayout();
            });
            tagLayout.addView(view);
        }
    }

    private void searchEdit(String searchStr) {
        if (searchStr.length() > 0 && !searchStr.matches("^[ ]*$")) {
            findViewById(R.id.search_hint).setVisibility(View.GONE);
            //List<SearchBean> beans = FileUtils.fuzzySearch(s.toString(),db.getData());
            if (!db.isTableExist() || db.countLocation() == 0) {
                Log.d(TAG, "edit search insertDB");
                SearchManager.getInstance().insertDB();
            }
            List<SearchBean> beans = db.getData1(searchStr);
            if (beans.size() == 0) {
                findViewById(R.id.rcvSearch).setVisibility(View.GONE);
                findViewById(R.id.historical_layout).setVisibility(View.GONE);
                findViewById(R.id.list_hint).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rcvSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.list_hint).setVisibility(View.GONE);
                findViewById(R.id.historical_layout).setVisibility(View.GONE);
                adapter = new SearchAdapter(LauncherSearchActivity.this, beans, searchStr);
                adapter.setOnItemClickListerner(LauncherSearchActivity.this);
                rcvSearch.setAdapter(adapter);
            }
        } else {
            if (db.getHistoricalData().size() == 0) {
                findViewById(R.id.historical_layout).setVisibility(View.GONE);
            } else {
                findViewById(R.id.historical_layout).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.search_hint).setVisibility(View.VISIBLE);
            findViewById(R.id.rcvSearch).setVisibility(View.GONE);
            findViewById(R.id.list_hint).setVisibility(View.GONE);
        }
    }

}