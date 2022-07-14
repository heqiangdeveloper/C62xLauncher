package launcher.base.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(getLayoutId(), container, false);
        initViews(mRootView);
        return mRootView;
    }

    protected abstract void initViews(View rootView);

    protected abstract int getLayoutId() ;
}
