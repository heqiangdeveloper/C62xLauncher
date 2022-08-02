package com.chinatsp.iquting;

import androidx.lifecycle.MutableLiveData;

import com.chinatsp.iquting.songs.IQuTingSong;
import com.chinatsp.iquting.state.IQuTingState;
import com.chinatsp.iquting.state.UnLoginState;

import java.util.LinkedList;
import java.util.List;

public class IQuTingController {
    private IQuTingCardView mView;
    private MutableLiveData<IQuTingState> mStateMutableLiveData = new MutableLiveData<>(new UnLoginState());
    public IQuTingController(IQuTingCardView view) {
        this.mView = view;
    }

    IQuTingState getState() {
        return mStateMutableLiveData.getValue();
    }

    void onDestroy() {
        mView = null;
    }

    public List<IQuTingSong> createTestList() {
        List<IQuTingSong> songList = new LinkedList<>();
        songList.add(new IQuTingSong());
        songList.add(new IQuTingSong());
        songList.add(new IQuTingSong());
        songList.add(new IQuTingSong());
        songList.add(new IQuTingSong());
        songList.add(new IQuTingSong());
        return songList;
    }
}
