package launcher.base.utils.selector;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class StatefulViewHolder  {
    private View rootView;
    private boolean isSelected;
    private String mTag;

    public static StatefulViewHolder create(@NonNull View rootView, @NonNull String tag) {
        StatefulViewHolder statefulViewHolder = new StatefulViewHolder();
        statefulViewHolder.rootView = rootView;
        statefulViewHolder.init();
        statefulViewHolder.mTag = tag;
        return statefulViewHolder;
    }

    private StatefulViewHolder() {

    }

    private void init() {
        if (rootView == null) {
            return;
        }

    }

    public boolean isSelected() {
        return isSelected;
    }



    public void changeViewsState(boolean isSelected) {
        if (rootView == null) {
            return;
        }
        rootView.setSelected(isSelected);

        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View childAt = group.getChildAt(i);
                if (childAt != null) {
                    childAt.setSelected(isSelected);
                }
            }
        }
    }

    public View getView() {
        return rootView;
    }

    public boolean check(View targetView){
        return rootView == targetView;
    }
    public boolean check(int viewId){
        return (rootView != null) && rootView.getId() == viewId;
    }

    public String getTag() {
        return mTag;
    }
}
