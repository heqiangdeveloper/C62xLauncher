package com.chinatsp.volcano;

import androidx.lifecycle.MutableLiveData;

import com.chinatsp.volcano.videos.VolcanoVideo;

import java.util.LinkedList;
import java.util.List;

public class VolcanoController {
    private VolcanoCardView mView;
    public VolcanoController(VolcanoCardView view) {
        this.mView = view;
    }


    void onDestroy() {
        mView = null;
    }

    public List<VolcanoVideo> createTestList() {
        List<VolcanoVideo> songList = new LinkedList<>();
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        songList.add(new VolcanoVideo());
        return songList;
    }
}
