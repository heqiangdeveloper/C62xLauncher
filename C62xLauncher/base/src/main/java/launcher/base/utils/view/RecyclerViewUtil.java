package launcher.base.utils.view;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewUtil {
    public static RecyclerView.ViewHolder findViewHold(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            return null;
        }
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return null;
        }
        if (adapter.getItemCount() <= position || position < 0) {
            return null;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return null;
        }
        return recyclerView.findViewHolderForAdapterPosition(position);
    }
}
