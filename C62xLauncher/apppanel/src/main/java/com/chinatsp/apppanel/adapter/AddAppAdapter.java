package com.chinatsp.apppanel.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.bean.LocationBean;

import java.util.Collections;
import java.util.List;

public class AddAppAdapter extends RecyclerView.Adapter<AddAppAdapter.ViewHolder> {
    private Context context;
    private List<LocationBean> infos;
    private LayoutInflater layoutInflater;
    public AddAppAdapter(Context context, List<LocationBean> infos) {
        this.context = context;
        this.infos = infos;
        infos.removeAll(Collections.singleton(null));//清除掉null对象
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.add_app_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(null == infos.get(position).getImgDrawable()){
            byte[] b = infos.get(position).getImgByte();
            holder.iconIv.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
        }else {
            holder.iconIv.setImageDrawable(infos.get(position).getImgDrawable());
        }
        holder.nameTv.setText(infos.get(position).getName());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout root;
        ImageView iconIv;
        ImageView selectIv;
        TextView nameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon_iv);
            selectIv = (ImageView) itemView.findViewById(R.id.select_iv);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            root = (LinearLayout) itemView.findViewById(R.id.container_ll);
        }
    }
}
