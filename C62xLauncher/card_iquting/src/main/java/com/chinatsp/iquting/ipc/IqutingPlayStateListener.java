package com.chinatsp.iquting.ipc;

public interface IqutingPlayStateListener {
    void onStart();
    void onPause();
    void onStop();
    void onProgress(String s, long l, long l1);
    void onBufferingStart();
    void onBufferingEnd();
    void onPlayError(int i, String s);
    void onAudioSessionId(int i);
}
