package com.anarchy.classifyview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.VelocityTracker;
import android.view.View;


import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.ClassifyView;
import com.anarchy.classifyview.util.L;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/1 15:34
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public abstract class BaseSubAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements SubRecyclerViewCallBack {
    private final static int VELOCITY = 5;
    @Override
    public boolean canDragOnLongPress(int position, View pressedView) {
        return true;
    }

    private int mSelectedPosition = -1;
    @Override
    public void setDragPosition(int position) {
        if(position >= getItemCount()||position<-1) return;
        if(position == -1 && mSelectedPosition != -1){
//            int oldPosition = mSelectedPosition;
            mSelectedPosition = position;
            notifyDataSetChanged();
//            notifyItemChanged(oldPosition);
        }else {
            mSelectedPosition = position;
            notifyItemChanged(mSelectedPosition);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if(position == mSelectedPosition){
            holder.itemView.setVisibility(View.INVISIBLE);
        }else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public boolean canDropOver(int selectedPosition, int targetPosition) {
        //解决sub中，拖动应用图标时可以拖动到添加按钮的位置
        if(targetPosition == getItemCount() - 1){
            return false;
        }
        return true;
    }

    @Override
    public boolean canDragOut(int selectedPosition) {
        return true;
    }

    @Override
    public void moved(int selectedPosition, int targetPosition) {

    }
    @Override
    public int getCurrentState(View selectedView, View targetView, int x, int y,
                               VelocityTracker velocityTracker, int selectedPosition,
                               int targetPosition) {
        if(velocityTracker == null) return ClassifyView.STATE_NONE;
        int left = x;
        int top = y;
        int right = left + selectedView.getWidth();
        int bottom = top + selectedView.getHeight();
        if((Math.abs(left - targetView.getLeft())+Math.abs(right - targetView.getRight())+
                Math.abs(top - targetView.getTop())+ Math.abs(bottom - targetView.getBottom()))
                <(targetView.getWidth()+targetView.getHeight()
        )/2){
            velocityTracker.computeCurrentVelocity(100);
            float xVelocity = velocityTracker.getXVelocity();
            float yVelocity = velocityTracker.getYVelocity();
            float limit = getVelocity(targetView.getContext());
            if(xVelocity < limit && yVelocity < limit){
                return ClassifyView.STATE_MOVE;
            }
        }
        return ClassifyView.STATE_NONE;
    }

    @Override
    public float getVelocity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return density*VELOCITY + .5f;
    }

    @Override
    public void addItem(int position) {

    }

    @Override
    public void removeItem(int position) {

    }

    @Override
    public void notifyDataChanged() {
        notifyDataSetChanged();
    }
}

