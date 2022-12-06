package com.chinatsp.volcano.videos;

import com.chinatsp.volcano.R;
import com.chinatsp.volcano.repository.VolcanoRepository;

public class VolcanoSource {

    private String source;
    private String name;
    private int iconRes;

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    private int nameRes;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public static VolcanoSource create(String  source) {
        int iconRes;
        int nameRes;
        String sourceName;
        switch (source) {
            case VolcanoRepository.SOURCE_DOUYIN:
                iconRes = R.drawable.card_volcano_type_douyin;
                sourceName = "抖音";
                nameRes = R.string.card_volcano_title_douyin;
                break;
            case VolcanoRepository.SOURCE_XIGUA:
                iconRes = R.drawable.card_volcano_type_xigua;
                sourceName = "西瓜";
                nameRes = R.string.card_volcano_title_watermelon;
                break;
            case VolcanoRepository.SOURCE_TOUTIAO:
            default:
                iconRes = R.drawable.card_volcano_type_toutiao;
                sourceName = "头条";
                nameRes = R.string.card_volcano_title_head_line;
        }
        VolcanoSource volcanoSource = new VolcanoSource();
        volcanoSource.setSource(source);
        volcanoSource.setName(sourceName);
        volcanoSource.setIconRes(iconRes);
        volcanoSource.setNameRes(nameRes);
        return volcanoSource;
    }
}
