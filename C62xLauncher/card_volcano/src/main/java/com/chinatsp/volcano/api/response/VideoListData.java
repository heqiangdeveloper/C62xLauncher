package com.chinatsp.volcano.api.response;

import com.chinatsp.volcano.videos.VolcanoVideo;

import java.util.List;

public class VideoListData {
    private int total;
    private int update_interval;
    private List<VolcanoVideo> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUpdate_interval() {
        return update_interval;
    }

    public void setUpdate_interval(int update_interval) {
        this.update_interval = update_interval;
    }

    public List<VolcanoVideo> getList() {
        return list;
    }

    public void setList(List<VolcanoVideo> list) {
        this.list = list;
    }
}
