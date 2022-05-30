package com.chinatsp.widgetcards.editor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.adapter.BaseCardEntity;

import launcher.base.utils.EasyLog;

public class EditorHomeCardViewHolder extends BaseViewHolder<BaseCardEntity> {
    private ImageView mIcon;
    private TextView mName;
    private String Tag = "EditorHomeCardViewHolder";

    public EditorHomeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.ivCardSelectedLogo);
        mName = itemView.findViewById(R.id.tvCardSelectName);
    }

    @Override
    public void bind(int position, BaseCardEntity baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(baseCardEntity.getName());
        EasyLog.d(Tag, "bind "+baseCardEntity.getName());
    }
}
