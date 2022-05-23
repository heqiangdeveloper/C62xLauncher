package com.chinatsp.widgetcards.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseRcvAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<T> mData = new LinkedList<>();

    public BaseRcvAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(getLayoutRes(), parent, false);
        return new BaseViewHolder(view);
    }

    protected abstract int getLayoutRes();

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(position , mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<T> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
    }
}
