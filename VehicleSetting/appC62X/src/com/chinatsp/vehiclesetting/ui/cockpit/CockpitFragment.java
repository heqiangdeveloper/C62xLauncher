package com.chinatsp.vehiclesetting.ui.cockpit;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chinatsp.vehiclesetting.R;
import com.chinatsp.vehiclesetting.base.BaseFragment;
import com.chinatsp.vehiclesetting.databinding.FragmentCockpitBinding;

/**
 * 座舱
 */
public class CockpitFragment extends BaseFragment<FragmentCockpitBinding> {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* Fragment fragment = new CokpitFragmentPage();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit();*/
    }
}
