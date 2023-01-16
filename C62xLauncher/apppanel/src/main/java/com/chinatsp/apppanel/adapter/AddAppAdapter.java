package com.chinatsp.apppanel.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.Bean.LocationBean;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.event.SelectedCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import launcher.base.applists.AppLists;
import launcher.base.utils.glide.RoundCornerImage;

public class AddAppAdapter extends RecyclerView.Adapter<AddAppAdapter.ViewHolder> {
    private Context context;
    private List<LocationBean> infos;
    private LayoutInflater layoutInflater;
    private int parentIndex;
    private SelectedCallback selectedCallback;
    private List<LocationBean> selectdItems = new ArrayList<>();
    public AddAppAdapter(Context context, List<LocationBean> infos,int parentIndex,SelectedCallback selectedCallback) {
        this.context = context;
        this.infos = infos;
        this.parentIndex = parentIndex;
        this.selectedCallback = selectedCallback;
        infos.removeAll(Collections.singleton(null));//清除掉null对象
        layoutInflater = LayoutInflater.from(context);
        for (LocationBean info: infos) {
            if(info.getParentIndex() == parentIndex){
                selectdItems.add(info);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.add_app_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawable drawable;
        if(null == infos.get(position).getImgDrawable()){
            byte[] b = infos.get(position).getImgByte();
            drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
        }else {
            drawable = infos.get(position).getImgDrawable();
        }
        if(drawable != null){
            if(!AppLists.isSystemApplication(context,infos.get(position).getPackageName())){
                RoundCornerImage.crop(context,drawable,holder.iconIv);
            }else {
                holder.iconIv.setImageDrawable(drawable);
            }
        }else {
            drawable = context.getDrawable(R.drawable.ic_launcher);
            holder.iconIv.setImageDrawable(drawable);
        }
        holder.nameTv.setText(infos.get(position).getName());
        if(selectdItems.contains(infos.get(position))){
            holder.selectIv.setSelected(true);
        }else {
            holder.selectIv.setSelected(false);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.selectIv.isSelected()){
                    holder.selectIv.setSelected(false);
                    selectdItems.remove(infos.get(holder.getAdapterPosition()));
                }else {
                    holder.selectIv.setSelected(true);
                    selectdItems.add(infos.get(holder.getAdapterPosition()));
                }
                selectedCallback.onSelect(selectdItems.size() + "/" + infos.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public List<LocationBean> getSelectdItems(){
        return selectdItems;
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
