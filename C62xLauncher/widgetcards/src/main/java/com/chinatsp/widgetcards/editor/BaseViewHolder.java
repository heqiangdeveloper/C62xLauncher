package com.chinatsp.widgetcards.editor;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder<T>  extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(int position, T t) {

    }
}
