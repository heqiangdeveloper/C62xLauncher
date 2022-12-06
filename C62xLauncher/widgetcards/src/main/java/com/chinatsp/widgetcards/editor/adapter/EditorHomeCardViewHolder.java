package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.CardEditorResUtil;
import com.chinatsp.widgetcards.manager.CardNameRes;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorHomeCardViewHolder extends BaseViewHolder<LauncherCard> {
    private ImageView mIcon;
    private View mIvBg;
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
    public void bind(int position, LauncherCard card) {
        super.bind(position, card);
        mName.setText(CardNameRes.getStringRes(card.getType()));
        int iconRes = CardEditorResUtil.getIcon(card.getType());
        if (iconRes > 0) {
            mIcon.setImageResource(iconRes);
        } else {
            mIcon.setImageDrawable(null);
        }
        itemView.setVisibility(View.VISIBLE);
        mIvBgEmpty.setVisibility(View.GONE);
    }
}
