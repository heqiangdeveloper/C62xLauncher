package com.chinatsp.vehiclesetting.utils;

import android.view.View;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MutiClickListener implements View.OnClickListener
{
    private long lastclick = 0;
    private long times = 1000;
    private View.OnClickListener origin;

    public MutiClickListener(View.OnClickListener origin)
    {
        this.origin = origin;
    }

    @Override
    public void onClick(View v)
    {
        if (System.currentTimeMillis() - lastclick >= times)
        {
            origin.onClick(v);
            lastclick=System.currentTimeMillis();
        }else {

        }
    }
}
