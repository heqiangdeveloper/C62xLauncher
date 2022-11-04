package launcher.base.utils.view;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import launcher.base.utils.EasyLog;

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
        RecyclerView.ViewHolder viewHolder =  recyclerView.findViewHolderForAdapterPosition(position);
//        if (viewHolder == null) {
//            RecyclerView.RecycledViewPool pool = recyclerView.getRecycledViewPool();
//            int recycledViewCount = pool.getRecycledViewCount(type);
//            EasyLog.d("RecyclerViewUtil", "findViewHold, recycledViewCount:"+recycledViewCount);
//            viewHolder = pool.getRecycledView(type);
//            try {
//                pool.putRecycledView(viewHolder);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return viewHolder;
    }
}
