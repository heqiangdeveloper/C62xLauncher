package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.service.CardsTypeManager;

import launcher.base.recyclerview.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorUnselectCardViewHolder extends BaseViewHolder<BaseCardEntity> {
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
    public void bind(int position, BaseCardEntity baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(baseCardEntity.getName());
        mIvBg.setImageResource(baseCardEntity.getUnselectBgRes());

        EasyLog.d(Tag, "bind "+baseCardEntity.getName());
        if (baseCardEntity.getType() != CardsTypeManager.CardType.EMPTY) {
            mIvBg.setVisibility(View.VISIBLE);
            mIvBgEmpty.setVisibility(View.GONE);
        } else {
            mIvBg.setVisibility(View.GONE);
            mIvBgEmpty.setVisibility(View.VISIBLE);
        }
    }
}
