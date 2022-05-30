package com.chinatsp.vehicle.settings.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chinatsp.vehicle.settings.R;
import java.util.Random;

public class SimpleTabFragment extends Fragment {
    private static final String TAG = "SimpleTabFragment";

    private static final String KEY_TITLE = "title";

    TextView tvTitle;
    TextView tvExplain;


    String title = "Title";


    public static SimpleTabFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        SimpleTabFragment fragment = new SimpleTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach:" + title);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach:" + title);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged:" + title + ", hidden:" + hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:" + title);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:" + title);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(KEY_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_tab, container, false);
        tvTitle = view.findViewById(R.id.tv_title);
        tvExplain = view.findViewById(R.id.tv_explain);
        initView();
        return view;
    }

    private void initView() {
        int randomNumber = new Random().nextInt(1000);
        tvTitle.setText(String.format("这个是%s页面的内容", title));
        tvExplain.setText(String.format("这个是页面随机生成的数字:%d", randomNumber));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView:" + title);
    }
}
