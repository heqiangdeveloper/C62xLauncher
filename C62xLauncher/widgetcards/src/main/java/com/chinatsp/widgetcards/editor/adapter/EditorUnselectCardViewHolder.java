package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.manager.CardNameRes;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorUnselectCardViewHolder extends BaseViewHolder<LauncherCard> {
    private ImageView mIcon;
    private TextView mName;
    private ImageView mIvBg;
    private ImageView mIvBgEmpty;

    private String Tag = "EditorUnselectCardViewHolder";

    public EditorUnselectCardViewHolder(@NonNull View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.ivCardSelectedLogo);
        mName = itemView.findViewById(R.id.tvCardSelectName);
        mIvBg = itemView.findViewById(R.id.ivCardSelectedBg);
        mIvBgEmpty = itemView.findViewById(R.id.ivCardSelectedEmptyBg);
    }

    @Override
    public void bind(int position, LauncherCard baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(CardNameRes.getStringRes(baseCardEntity.getType()));
        mIvBg.setImageResource(baseCardEntity.getUnselectBgRes());

        if (baseCardEntity.getType() != CardManager.CardType.EMPTY) {
            mIvBg.setVisibility(View.VISIBLE);
            mIvBgEmpty.setVisibility(View.GONE);
        } else {
            mIvBg.setVisibility(View.GONE);
            mIvBgEmpty.setVisibility(View.VISIBLE);
        }
    }
}
