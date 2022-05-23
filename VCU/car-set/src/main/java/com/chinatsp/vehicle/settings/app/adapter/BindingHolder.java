package com.chinatsp.vehicle.settings.app.adapter;

import android.view.View;

import com.king.base.adapter.holder.ViewHolder;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 *
 */
public class BindingHolder<VDB extends ViewDataBinding> extends ViewHolder {

    VDB mBinding;

    public BindingHolder(View convertView) {
        super(convertView);
        mBinding = DataBindingUtil.bind(convertView);

    }

    public VDB getBinding() {
        return mBinding;
    }
}
