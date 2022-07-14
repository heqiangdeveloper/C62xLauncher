package launcher.base.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import launcher.base.utils.EasyLog;

public abstract class MultiStyleRcvAdapter<T extends BaseEntity> extends RecyclerView.Adapter<BaseViewHolder<T>> {
    private static final String TAG = "MultiStyleRcvAdapter";
    private List<T> mEntityList = new LinkedList<>();
    private Map<Integer, BaseEntity> mViewTypeMap = new HashMap<>();


    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MultiStyleRcvAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseEntity baseEntity = mViewTypeMap.get(viewType);
        if (baseEntity == null) {
            return createViewHolder(new View(mContext), viewType);
        }
        View view = mLayoutInflater.inflate(baseEntity.getItemLayoutId(), parent, false);
        return createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
        holder.bind(position, mEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        T t = mEntityList.get(position);
        return t.getViewType();
    }

    public void addEntities(List<T> entities) {
        mEntityList.clear();
        if (entities != null) {
            mEntityList = entities;
        }
        for (T t : mEntityList) {
            EasyLog.d(TAG, "addEntity type:" + t.getViewType());
            mViewTypeMap.put(t.getViewType(), t);
        }
    }

    protected abstract BaseViewHolder<T> createViewHolder(View view, int viewType);

}
