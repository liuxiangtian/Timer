package com.lxt.xiang.timer.listener;


public interface OnPlayStateChangeListener {
    void onMetaChange();
    void onMetaPlay();
    void onMetaPause();
    void onMetaStop();
    void onPrepare();
}
