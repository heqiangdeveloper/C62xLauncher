package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorHomeCardViewHolder extends BaseViewHolder<LauncherCard> {
    private ImageView mIcon;
    private ImageView mIvBg;
    private ImageView mIvBgEmpty;
    private TextView mName;
    private String Tag = "EditorHomeCardViewHolder";

    public EditorHomeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.ivCardSelectedLogo);
        mName = itemView.findViewById(R.id.tvCardSelectName);
        mIvBg = itemView.findViewById(R.id.ivCardSelectedBg);
        mIvBgEmpty = itemView.findViewById(R.id.ivCardSelectedEmptyBg);
    }

    @Override
    public void bind(int position, LauncherCard baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(baseCardEntity.getName());
        mIvBg.setImageResource(baseCardEntity.getSelectBgRes());
        mIvBg.setVisibility(View.VISIBLE);
        mIvBgEmpty.setVisibility(View.GONE);
    }
}
