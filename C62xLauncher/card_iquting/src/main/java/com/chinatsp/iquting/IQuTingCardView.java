package com.chinatsp.iquting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.iquting.state.NormalState;

import java.util.PrimitiveIterator;

import card.service.ICardStyleChange;
import launcher.base.utils.glide.GlideHelper;


public class IQuTingCardView extends ConstraintLayout implements ICardStyleChange {
    private static final String TAG = "WeatherCardLargeView";

    public IQuTingCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private IQuTingController mController;
    private NormalViewHolder mNormalViewHolder;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting, this);
        mNormalViewHolder = new NormalViewHolder();
        mController = new IQuTingController(this);
        new NormalState().updateViewState(this);
        mNormalViewHolder.updateMediaInfo();
    }

    @Override
    public void expand() {

    }

    @Override
    public void collapse() {

    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private class NormalViewHolder{
        private ImageView mIvCover;
        NormalViewHolder(){
            mIvCover = findViewById(R.id.ivIQuTingCover);
        }

        void updateMediaInfo() {
            GlideHelper.loadImageUrlAlbumCover(getContext(),mIvCover, R.drawable.test_cover,10);
        }
    }


}
