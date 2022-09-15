package com.chinatsp.apppanel;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import com.chinatsp.apppanel.fragments.AppStoreFragment;
import com.chinatsp.apppanel.fragments.MyAppFragment;

import java.util.ArrayList;
import java.util.List;

public class ApppanelActivity extends AppCompatActivity implements View.OnClickListener{
    private FrameLayout myAppLayout;
    private FrameLayout appStoreLayout;
    private ImageView myAppLine;
    private ImageView appStoreLine;
    private ImageView appPanelClose;

    private MyAppFragment myAppFragment;
    private AppStoreFragment appStoreFragment;
    private FragmentManager fm = getSupportFragmentManager();
    private List<Fragment> fragmentList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apppanel_layout);
        initView();
    }

    private void initView() {
        myAppLayout = (FrameLayout) findViewById(R.id.my_app_layout);
        appStoreLayout = (FrameLayout) findViewById(R.id.app_store_layout);
        myAppLine = (ImageView) findViewById(R.id.my_app_line);
        appStoreLine = (ImageView) findViewById(R.id.app_store_line);
        appPanelClose = (ImageView) findViewById(R.id.apppanel_close);
        //myAppLayout.setOnClickListener(this);
        //appStoreLayout.setOnClickListener(this);
        appPanelClose.setOnClickListener(this);

        //myAppLayout.callOnClick();
        loadData();
    }

    private void loadData(){
        myAppLine.setVisibility(View.VISIBLE);
        myAppFragment = new MyAppFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, myAppFragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        appStoreLine.setVisibility(View.INVISIBLE);
        myAppLine.setVisibility(View.INVISIBLE);
        if(v.getId() == R.id.my_app_layout){
            myAppLine.setVisibility(View.VISIBLE);

//            fragmentList = fm.getFragments();
//            if(fragmentList != null && fragmentList.size() != 0){
//                for(Fragment fragment : fragmentList){
//                    ft.remove(fragment);
//                }
//            }

            myAppFragment = new MyAppFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, myAppFragment);
            ft.commit();
        }else if(v.getId() == R.id.app_store_layout){
            appStoreLine.setVisibility(View.VISIBLE);

//            fragmentList = fm.getFragments();
//            if(fragmentList != null && fragmentList.size() != 0){
//                for(Fragment fragment : fragmentList){
//                    ft.remove(fragment);
//                }
//            }

            appStoreFragment = new AppStoreFragment();
            FragmentTransaction ft1 = fm.beginTransaction();
            ft1.replace(R.id.fragment_container, appStoreFragment);
            ft1.commit();
        }else if(v.getId() == R.id.apppanel_close){
            finish();
        }
    }
}
