package com.anarchy.classifyview.adapter;
import android.content.Context;
import android.view.VelocityTracker;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.ChangeInfo;
import com.anarchy.classifyview.ClassifyView;

import java.util.List;

/**
 * <p/>
 * Date: 16/6/1 15:33
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public abstract class BaseMainAdapter<VH extends RecyclerView.ViewHolder, Sub extends SubRecyclerViewCallBack> extends RecyclerView.Adapter<VH> implements MainRecyclerViewCallBack<Sub> {
    private final static int VELOCITY = 5;
    private int mSelectedPosition = -1;

    @Override
    public void setDragPosition(int position) {
        if (position >= getItemCount() || position < -1) return;
        if (position == -1 && mSelectedPosition != -1) {
            int oldPosition = mSelectedPosition;
            mSelectedPosition = position;
            notifyItemChanged(oldPosition);
        } else {
            mSelectedPosition = position;
            notifyItemChanged(mSelectedPosition);
        }
    }


    @Override
    public boolean onMergeStart(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        return onMergeStart(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }

    @Override
    public void onMergeCancel(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        onMergeCancel(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }

    @Override
    public void onMerged(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        onMerged(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }

    @Override
    public ChangeInfo onPrepareMerge(RecyclerView parent, int selectedPosition, int targetPosition) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        return onPrePareMerge(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition);
    }

    @Override
    public void onStartMergeAnimation(RecyclerView parent, int selectedPosition, int targetPosition,int duration) {
        VH selectedViewHolder = (VH) parent.findViewHolderForAdapterPosition(selectedPosition);
        VH targetViewHolder = (VH) parent.findViewHolderForAdapterPosition(targetPosition);
        onStartMergeAnimation(selectedViewHolder, targetViewHolder, selectedPosition, targetPosition,duration);
    }

    public abstract boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onMergeCancel(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onNotifyAll();//???????????????item??????

    public abstract void onMerged(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract ChangeInfo onPrePareMerge(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition);

    public abstract void onStartMergeAnimation(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition,int duration);

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (position == mSelectedPosition) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public boolean canDropOVer(int selectedPosition, int targetPosition) {
        return true;
    }


//    @Override
//    public int getCurrentState(View selectedView, View targetView, int x, int y,
//                               VelocityTracker velocityTracker, int selectedPosition,
//                               int targetPosition) {
//        if (velocityTracker == null) return ClassifyView.STATE_NONE;
//        int left = x;
//        int top = y;
//        int right = left + selectedView.getWidth();
//        int bottom = top + selectedView.getHeight();
//        if (canMergeItem(selectedPosition, targetPosition)) {
//            if ((Math.abs(left - targetView.getLeft()) + Math.abs(right - targetView.getRight()) +
//                    Math.abs(top - targetView.getTop()) + Math.abs(bottom - targetView.getBottom()))
//                    < (targetView.getWidth() + targetView.getHeight()
//            ) / 3) {
//                return ClassifyView.STATE_MERGE;
//            }
//        }
//        if ((Math.abs(left - targetView.getLeft()) + Math.abs(right - targetView.getRight()) +
//                Math.abs(top - targetView.getTop()) + Math.abs(bottom - targetView.getBottom()))
//                < (targetView.getWidth() + targetView.getHeight()
//        ) / 2) {
//            velocityTracker.computeCurrentVelocity(100);
//            float xVelocity = velocityTracker.getXVelocity();
//            float yVelocity = velocityTracker.getYVelocity();
//            float limit = getVelocity(targetView.getContext());
//            if (xVelocity < limit && yVelocity < limit) {
//                return ClassifyView.STATE_MOVE;
//            }
//        }
//        return ClassifyView.STATE_NONE;
//    }

    //??????????????????https://blog.csdn.net/zou249014591/article/details/105013075
    @Override
    public int getCurrentState(View selectedView, View targetView, int x, int y,
                               VelocityTracker velocityTracker, int selectedPosition,
                               int targetPosition) {
        if (velocityTracker == null) return ClassifyView.STATE_NONE;
        int left = x;
        int top = y;
        int selectX= left + selectedView.getWidth()/2;
        int selectY= top + selectedView.getHeight()/2;
        int targetX= targetView.getLeft() + targetView.getWidth()/2;
        int targetY= targetView.getTop() + targetView.getHeight()/2;
        /**
         * ???????????????
         * ???????????? ??????????????????
         * ?????????????????????A???????????????????????????????????????????????????B???
         * ??????????????? None>merge>move>None
         */
        boolean canMerge = canMergeItem(selectedPosition, targetPosition);
        int distance = getDistance(selectX, selectY, targetX, targetY);
        //????????????1/3??????
        if(canMerge && distance < targetView.getWidth()/3){
            return ClassifyView.STATE_MERGE;
        }
        //????????????1/3????????????1/2??????
        if(distance < targetView.getWidth()/2){
            if(selectedPosition <= targetPosition){
                //select????????????target?????????
                if(canMerge && (targetX-selectX+targetY-selectY) > 0){
                    //select?????????target?????????
                    return ClassifyView.STATE_NONE;
                }else {
                    return ClassifyView.STATE_MOVE;
                }
            }else {
                //selectet????????????target?????????
                if(canMerge && (targetX-selectX+targetY-selectY) < 0){
                    //select?????????target?????????
                    return ClassifyView.STATE_NONE;
                }else {
                    return ClassifyView.STATE_MOVE;
                }
            }
        }
        //????????????1/2??????????????????
        if(distance < targetView.getWidth()){
            if(selectedPosition <= targetPosition){
                if((selectX-targetX+selectY-targetY) > 0){
                    //select????????????target?????????,?????????target?????????
                    return ClassifyView.STATE_MOVE;
                }
            }else {
                if((targetX-selectX+targetY-selectY) > 0){
                    //select????????????target?????????,?????????target?????????
                    return ClassifyView.STATE_MOVE;
                }
            }
        }
        return ClassifyView.STATE_NONE;
    }

    private int getDistance(int selectX,int selectY,int targetX,int targetY){
        return (int) Math.hypot(selectX-targetX,selectY-targetY);
    }

    @Override
    public float getVelocity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return density * VELOCITY + .5f;
    }

    @Override
    public void moved(int selectedPosition, int targetPosition) {

    }

    @Override
    public void addItem(int position) {

    }

    @Override
    public void removeItem(int position) {

    }

    @Override
    public int total() {
        return 0;
    }
}

