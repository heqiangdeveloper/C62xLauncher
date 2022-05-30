package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.editor.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorUnselectCardViewHolder extends BaseViewHolder<BaseCardEntity> {
    private ImageView mIcon;
    private TextView mName;
    private String Tag = "EditorUnselectCardViewHolder";

    public EditorUnselectCardViewHolder(@NonNull View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.ivCardSelectedLogo);
        mName = itemView.findViewById(R.id.tvCardSelectName);
    }

    @Override
    public void bind(int position, BaseCardEntity baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(baseCardEntity.getName());
        itemView.setBackgroundResource(baseCardEntity.getUnselectBgRes());
        EasyLog.d(Tag, "bind "+baseCardEntity.getName());
    }
}
