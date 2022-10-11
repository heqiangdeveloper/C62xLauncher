package com.chinatsp.drawer.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.apps.AppsDrawerViewHelper;
import com.chinatsp.drawer.bean.SearchBean;
import com.chinatsp.drawer.search.utils.FileUtils;
import com.chinatsp.widgetcards.R;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchBean> searchBeanList;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListerner mOnItemClickListerner;
    private String searchStr;//变色的字符串

    public SearchAdapter(Context context, List<SearchBean> searchBeanList, String searchStr) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.searchBeanList = searchBeanList;
        this.searchStr = searchStr;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.adapter_search, null);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        //holder.name.setText(searchBeanList.get(position).getChineseFunction());
        holder.itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemSearch.setBackgroundColor(context.getResources().getColor(R.color.search_item_bg));
                holder.secondaryName.setTextColor(context.getResources().getColor(R.color.search_item_text));
                holder.iconArrow.setImageDrawable(context.getDrawable(R.drawable.icon_arrow_right_48_pre));
                mOnItemClickListerner.onItemClick(position, searchBeanList.get(position));
            }
        });
       String [] str = new String[searchStr.length()];
        for (int i = 0; i < searchStr.length(); i++) {
            str[i] = searchStr.charAt(i) + "";
        }
        if(FileUtils.getLanguage() ==1){
            holder.secondaryName.setText(searchBeanList.get(position).getChineseFunctionLevel());
            holder.name.append(FileUtils.fillColor(context,searchBeanList.get(position).getChineseFunction(),str,R.color.search_item_bg));
        }else{
            holder.secondaryName.setText(searchBeanList.get(position).getEnglishFunctionLevel());
            holder.name.append(FileUtils.fillColor(context,searchBeanList.get(position).getEnglishFunction(),str,R.color.search_item_bg));
        }

    }

    @Override
    public int getItemCount() {
        //return recentAppInfos.size();
        return searchBeanList.size() > AppsDrawerViewHelper.MAX_NUM ? AppsDrawerViewHelper.MAX_NUM : searchBeanList.size();
    }

    public void setData(List<SearchBean> appInfos) {
        if (appInfos == null || appInfos.isEmpty()) {
            return;
        }
        if (searchBeanList == null) {
            searchBeanList = new ArrayList<>();
        } else {
            searchBeanList.clear();
        }
        searchBeanList.addAll(appInfos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView secondaryName;
        RelativeLayout itemSearch;
        ImageView iconArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            secondaryName = itemView.findViewById(R.id.secondaryName);
            itemSearch = itemView.findViewById(R.id.itemSearch);
            iconArrow = itemView.findViewById(R.id.iconArrow);
        }
    }

    public void setOnItemClickListerner(OnItemClickListerner onItemClickListerner) {
        mOnItemClickListerner = onItemClickListerner;
    }

    public interface OnItemClickListerner {
        void onItemClick(int position, SearchBean bean);
    }
}
