package com.chinatsp.widgetcards.editor.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.widgetcards.R;
import com.chinatsp.entity.BaseCardEntity;
import launcher.base.recyclerview.BaseViewHolder;

import launcher.base.utils.EasyLog;

public class EditorHomeCardViewHolder extends BaseViewHolder<BaseCardEntity> {
    private ImageView mIcon;
    private ImageView mIvBg;
    private TextView mName;
    private String Tag = "EditorHomeCardViewHolder";

    private float mDownX;
    private float mDownY;

    public EditorHomeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.ivCardSelectedLogo);
        mName = itemView.findViewById(R.id.tvCardSelectName);
        mIvBg = itemView.findViewById(R.id.ivCardSelectedBg);
    }

    @Override
    public void bind(int position, BaseCardEntity baseCardEntity) {
        super.bind(position, baseCardEntity);
        mName.setText(baseCardEntity.getName());
        mIvBg.setBackgroundResource(baseCardEntity.getSelectBgRes());

        EasyLog.d(Tag, "bind "+baseCardEntity.getName());
    }
}
