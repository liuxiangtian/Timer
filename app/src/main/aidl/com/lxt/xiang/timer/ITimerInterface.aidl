// ITimerInterface.aidl
package com.lxt.xiang.timer;

import com.lxt.xiang.timer.model.Track;

interface ITimerInterface {

    int getCurrentPosition();
    List<Track> getQueues();
    long[] getIds();
    int getQueueSize();
    Track getCurrentTrack();

    void play(in long[] ids, long id, int position);
    boolean isPlaying();
    long getSeekPosition();
    void start();
        void next();
        void prev();
        void pause();
        void seek(in long position);
}
