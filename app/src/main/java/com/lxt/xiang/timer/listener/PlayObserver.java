package com.lxt.xiang.timer.listener;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayObserver implements Parcelable {

    public void onMetaPlay(){}
    public void onMetaPause(){}

    public PlayObserver() {
    }

    protected PlayObserver(Parcel in) {
    }

    public static final Creator<PlayObserver> CREATOR = new Creator<PlayObserver>() {
        @Override
        public PlayObserver createFromParcel(Parcel in) {
            return new PlayObserver(in);
        }

        @Override
        public PlayObserver[] newArray(int size) {
            return new PlayObserver[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
