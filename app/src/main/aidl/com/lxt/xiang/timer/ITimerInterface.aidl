package com.lxt.xiang.timer;

import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.listener.PlayObserver;

interface ITimerInterface {

    int getCurrentPosition();
    List<Track> getQueues();
    long[] getIds();
    int getQueueSize();
    Track getCurrentTrack();
    long getDuration();

    void play(in long[] ids, long id, int position);
    boolean isPlaying();
    long getSeekPosition();
    void start();
        void next();
        void prev();
        void pause();
        void seek(in long position);

    void registerPlayObserver(in PlayObserver playObserver);
    void unRegisterPlayObserver(in PlayObserver playObserver);

}
