package com.chinatsp.drawer.iquting;

import java.util.LinkedList;
import java.util.List;

class IqutingController {
    private DrawerIqutingHolder mViewHolder;

    public IqutingController(DrawerIqutingHolder viewHolder) {
        mViewHolder = viewHolder;
    }

    void requestSongs() {
        List<SongItem> songItemList = new LinkedList<>();
        songItemList.add(new SongItem());
        songItemList.add(new SongItem());
        mViewHolder.showSongs(songItemList);
    }
}
