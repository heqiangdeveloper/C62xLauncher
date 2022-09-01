package com.chinatsp.volcano.videos;

import com.chinatsp.volcano.R;
import com.chinatsp.volcano.repository.VolcanoRepository;

public class VolcanoSource {

    private String source;
    private String name;
    private int iconRes;


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
        int res;
        String sourceName;
        switch (source) {
            case VolcanoRepository.SOURCE_DOUYIN:
                res = R.drawable.card_volcano_type_douyin;
                sourceName = "抖音";
                break;
            case VolcanoRepository.SOURCE_XIGUA:
                res = R.drawable.card_volcano_type_xigua;
                sourceName = "西瓜";
                break;
            case VolcanoRepository.SOURCE_TOUTIAO:
            default:
                res = R.drawable.card_volcano_type_toutiao;
                sourceName = "头条";
        }
        VolcanoSource volcanoSource = new VolcanoSource();
        volcanoSource.setSource(source);
        volcanoSource.setName(sourceName);
        volcanoSource.setIconRes(res);
        return volcanoSource;
    }
}
