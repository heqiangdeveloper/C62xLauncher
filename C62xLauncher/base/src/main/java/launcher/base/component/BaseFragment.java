package launcher.base.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(getLayoutId(), container, false);
        initViews(rootView);
        return rootView;
    }

    protected abstract void initViews(View rootView);

    protected abstract int getLayoutId() ;
}
