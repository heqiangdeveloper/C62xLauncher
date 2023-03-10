package com.anarchy.classifyview.simple;

import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.ChangeInfo;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.adapter.BaseMainAdapter;
import com.anarchy.classifyview.adapter.BaseSubAdapter;
import com.anarchy.classifyview.adapter.SubAdapterReference;
import com.anarchy.classifyview.event.ReStoreDataEvent;
import com.anarchy.classifyview.simple.widget.CanMergeView;
import com.anarchy.classifyview.util.L;
import com.anarchy.classifyview.util.MyConfigs;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Date: 16/6/7 11:55
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public abstract class SimpleAdapter<T, VH extends SimpleAdapter.ViewHolder> implements BaseSimpleAdapter {
    protected List<List<T>> mData;
    private SimpleMainAdapter mSimpleMainAdapter;
    private SimpleSubAdapter mSimpleSubAdapter;

    public SimpleAdapter(List<List<T>> data) {
        mData = data;
        mSimpleMainAdapter = new SimpleMainAdapter(this, mData);
        mSimpleSubAdapter = new SimpleSubAdapter(this);
    }

    @Override
    public BaseMainAdapter getMainAdapter() {
        return mSimpleMainAdapter;
    }

    @Override
    public BaseSubAdapter getSubAdapter() {
        return mSimpleSubAdapter;
    }

    protected VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item, parent, false);
        return (VH) new ViewHolder(view);
    }

    protected void onBindMainViewHolder(VH holder, int position) {
    }

    protected void onBindSubViewHolder(VH holder, int mainPosition,int subPosition) {
    }


    public void notifyItemInsert(int position){
        mSimpleMainAdapter.notifyItemInserted(position);
    }

    public void notifyItemChanged(int position){
        mSimpleMainAdapter.notifyItemChanged(position);
    }

    public void notifyItemRangeChanged(int position,int count){
        mSimpleMainAdapter.notifyItemRangeChanged(position,count);
    }

    public void notifyItemRangeInsert(int position,int count){
        mSimpleMainAdapter.notifyItemRangeInserted(position,count);
    }


    public void notifyDataSetChanged(){
        Log.d("MyAppFragment","notifyDataSetChanged ReStoreDataEvent");
        mSimpleMainAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new ReStoreDataEvent());//??????????????????
    }
    /**
     * @param parentIndex
     * @param index       if -1  in main region
     */
    protected void onItemClick(View view, int parentIndex, int index) {
    }

    /**
     * ????????????item?????????
     *
     * @return
     */
    public abstract View getView(ViewGroup parent, int mainPosition, int subPosition);

    public class SimpleMainAdapter extends BaseMainAdapter<VH, SimpleSubAdapter> {
        private List<List<T>> mData;
        private SimpleAdapter<T, VH> mSimpleAdapter;

        public SimpleMainAdapter(SimpleAdapter<T, VH> simpleAdapter, List<List<T>> data) {
            mData = data;
            mSimpleAdapter = simpleAdapter;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = mSimpleAdapter.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(mSimpleAdapter);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                Log.d("dragtest","onBindViewHolder position = " + position + ",mData.size() = " + mData.size());
                canMergeView.initMain(position, mData.get(position));
            }
            mSimpleAdapter.onBindMainViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public boolean canDragOnLongPress(int position, View pressedView) {
            return true;
        }



        @Override
        public boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder,
                                    int selectedPosition, int targetPosition) {
            L.d("on mergeStart:(%1$s,%2$s)",selectedPosition,targetPosition);
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                if(selectedPosition != targetPosition){
                    canMergeView.onMergeStart();
                }else {
                    //????????????????????????????????????????????????????????????????????????
                    canMergeView.onMergeCancel();
                }
            }
            return true;
        }

        @Override
        public void onMergeCancel(VH selectedViewHolder, VH targetViewHolder,
                                  int selectedPosition, int targetPosition) {
            L.d("on mergeCancel:(%1$s,%2$s)",selectedPosition,targetPosition);
            if(targetViewHolder == null){
                L.d("targetViewHolder == null");
                refreshAll();
            }else {
                CanMergeView canMergeView = targetViewHolder.getCanMergeView();
                if (canMergeView != null) {
                    canMergeView.onMergeCancel();
                }else {
                    refreshAll();
                }
            }
        }

        @Override
        public void onNotifyAll() {
            refreshAll();
        }

        private void refreshAll(){
            notifyDataSetChanged();
        }

        @Override
        public void onMerged(VH selectedViewHolder, VH targetViewHolder,
                             int selectedPosition, int targetPosition) {
            L.d("on Merged:(%1$s,%2$s)",selectedPosition,targetPosition);
            //A. by heqiang
            L.d("targetPosition = " + targetPosition);
            if(selectedPosition == targetPosition){
                //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                refreshAll();
                return;
            }
            CanMergeView canMergeView = null;
            if(targetViewHolder != null){
                canMergeView = targetViewHolder.getCanMergeView();
            }else{
                L.d("targetViewHolder is null");
            }
            if (canMergeView != null) {
                canMergeView.onMerged();
            }
            mData.get(targetPosition).add(mData.get(selectedPosition).get(0));
            mData.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
//            if(selectedPosition < targetPosition) {
//                notifyItemChanged(targetPosition-1);
//            }else {
//                notifyItemChanged(targetPosition);
//            }
            //??????????????????????????????APP??????????????????
            refreshAll();
            Log.d("MyAppFragment","onMerged ReStoreDataEvent");
            EventBus.getDefault().post(new ReStoreDataEvent());//??????????????????
        }

        @Override
        public ChangeInfo onPrePareMerge(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            if(targetViewHolder == null || selectedViewHolder == null) return null;
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                ChangeInfo info = canMergeView.prepareMerge();
                info.paddingLeft = selectedViewHolder.getPaddingLeft();
                info.paddingRight = selectedViewHolder.getPaddingRight();
                info.paddingTop = selectedViewHolder.getPaddingTop();
                info.paddingBottom = selectedViewHolder.getPaddingBottom();
                info.outlinePadding = canMergeView.getOutlinePadding();
                return info;
            }
            return null;
        }

        @Override
        public void onStartMergeAnimation(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition,int duration) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.startMergeAnimation(duration);
            }
        }


        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            List<T> list = mData.remove(selectedPosition);
            //List<T> list = mData.get(selectedPosition);
            //mData.remove(selectedPosition);
            mData.add(targetPosition, list);
            return true;
        }

        @Override
        public boolean canMergeItem(int selectedPosition, int targetPosition) {
            List<T> currentSelected = mData.get(selectedPosition);
            return currentSelected.size() < 2;
        }


        @Override
        public int onLeaveSubRegion(int selectedPosition, SubAdapterReference<SimpleSubAdapter> subAdapterReference) {
            SimpleSubAdapter simpleSubAdapter = subAdapterReference.getAdapter();
            T t = simpleSubAdapter.getData().remove(selectedPosition);
            List<T> list = new ArrayList<>();
            list.add(t);
            mData.add(list);
            int parentIndex = simpleSubAdapter.getParentIndex();
            if (parentIndex != -1) notifyItemChanged(parentIndex);
            return mData.size() - 1;
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            mSimpleAdapter.onItemClick(pressedView, position, -1);//-1 ?????????main area
        }

        @Override
        public List<T> explodeItem(int position, View pressedView) {
            Log.d("dragtest","position = " + position + ",mData.size() = " + mData.size());
            if (position < mData.size())
                return mData.get(position);
            return null;
        }

        @Override
        public void addItem(int position) {
            super.addItem(position);
        }

        @Override
        public void removeItem(int position) {
            mData.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int total() {
            return mData.size();
        }
    }

    class SimpleSubAdapter extends BaseSubAdapter<VH> {
        private List<T> mData;
        private int parentIndex = -1;
        private SimpleAdapter<T, VH> mSimpleAdapter;

        public SimpleSubAdapter(SimpleAdapter<T, VH> simpleAdapter) {
            mSimpleAdapter = simpleAdapter;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = mSimpleAdapter.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(mSimpleAdapter);
            }
            return vh;
        }

        public int getParentIndex() {
            return parentIndex;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.initSub(parentIndex,position);
            }
            mSimpleAdapter.onBindSubViewHolder(holder,parentIndex,position);
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            mSimpleAdapter.onItemClick(pressedView, parentIndex, position);
        }

        @Override
        public void initData(int parentIndex, List data) {
            mData = data;
            this.parentIndex = parentIndex;
            notifyDataSetChanged();
        }

        @Override
        public List getSubData() {
            return mData;
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            T t = mData.remove(selectedPosition);
            mData.add(targetPosition, t);
            if(parentIndex != -1) {
                mSimpleMainAdapter.notifyItemChanged(parentIndex);
            }
            return true;
        }

        public List<T> getData() {
            return mData;
        }

        @Override
        public void addItem(int position) {
            mData.add(position, (T) new ResolveInfo());
            notifyDataSetChanged();
        }

        @Override
        public void removeItem(int position) {
            if(position != -1 && position < mData.size()){
                mData.remove(position);
            }

            //??????
            if(parentIndex != -1) {
                mSimpleMainAdapter.notifyItemChanged(parentIndex);
            }
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected CanMergeView mCanMergeView;
        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CanMergeView) {
                mCanMergeView = (CanMergeView) itemView;
            } else if (itemView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) itemView;
                paddingLeft = group.getPaddingLeft();
                paddingRight = group.getPaddingRight();
                paddingTop = group.getPaddingTop();
                paddingBottom = group.getPaddingBottom();
                //??????????????? ??????????????????????????????view
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof CanMergeView) {
                        mCanMergeView = (CanMergeView) child;
                        break;
                    }
                }
            }
        }

        public CanMergeView getCanMergeView() {
            return mCanMergeView;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }
    }
}

