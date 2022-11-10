package com.chinatsp.apppanel.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.event.DeletedCallback;
import com.chinatsp.apppanel.utils.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AppManageAdapter extends RecyclerView.Adapter<AppManageAdapter.ViewHolder> {
    private Context context;
    private List<HashMap<String,Object>> infos;
    private LayoutInflater layoutInflater;
    private DeletedCallback deletedCallback;
    private static final String TAG = AppManageAdapter.class.toString();
    public AppManageAdapter(Context context, List<HashMap<String,Object>> infos,DeletedCallback deletedCallback) {
        this.context = context;
        this.infos = infos;
        this.deletedCallback = deletedCallback;
        infos.removeAll(Collections.singleton(null));//清除掉null对象
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.appmanage_list_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String packageName = (String) infos.get(position).get("packageName");

        holder.iconIv.setImageDrawable((Drawable) infos.get(position).get("icon"));
        holder.nameTv.setText((String) infos.get(position).get("title"));
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.forceStopPackage(context,(String) infos.get(holder.getAdapterPosition()).get("packageName"));
                if(infos.size() == 1){
                    deletedCallback.onDeleted();
                }else {
                    for(int i = 0; i < infos.size(); i++){
                        if(packageName.equals((String) infos.get(i).get("packageName"))){
                            infos.remove(i);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
        holder.iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.launchApp(context,packageName);
                deletedCallback.onDeleted();
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public List<HashMap<String,Object>> getInfos(){
        return infos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout root;
        public ImageView iconIv;
        public ImageView deleteIv;
        public TextView nameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon_iv);
            deleteIv = (ImageView) itemView.findViewById(R.id.delete_iv);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            root = (LinearLayout) itemView.findViewById(R.id.container_ll);
        }
    }
}
