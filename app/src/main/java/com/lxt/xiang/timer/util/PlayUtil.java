package com.lxt.xiang.timer.util;


import android.os.RemoteException;
import android.util.Log;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.model.Track;

import org.joda.time.Duration;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class PlayUtil {

    public static void playTracks(final BaseActivity baseActivity) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
    }

    public static void playTracks(final BaseActivity baseActivity, long[] ids, long id, int position) {
        Log.i("main", "playTracks: " + id);
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            iTimerService.play(ids,id,position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void togglePlay(final BaseActivity baseActivity) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            if(iTimerService.isPlaying()){
                iTimerService.pause();
            }else {
                iTimerService.start();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playNext(final BaseActivity baseActivity) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            iTimerService.next();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playPrev(final BaseActivity baseActivity) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            iTimerService.prev();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void seek(final BaseActivity baseActivity, int progress) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            Track track = iTimerService.getCurrentTrack();
            if (track != null) {
                long duration = track.getDuration();
                iTimerService.seek(progress*duration/ConstantsUtil.PROCESS_MAX);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void responsePlay(BaseActivity baseActivity, TrackAdaptor trackAdaptor) {
        if (baseActivity == null) return;
        ITimerInterface iTimerInterface = baseActivity.iTimerService;
        if (iTimerInterface == null) {
            return;
        }
        try {
            int playPosition = iTimerInterface.getCurrentPosition();
            trackAdaptor.notifyItem(playPosition, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String durationToString(long duration) {
        Duration d = new Duration(duration);
        Minutes minutes = d.toStandardMinutes();
        Seconds seconds = d.toStandardSeconds();
        int minute = minutes.getMinutes();
        int second = seconds.getSeconds()%60;
        StringBuilder builder = new StringBuilder();
        if(minute>=0 && minute<=9){
            builder.append("0").append(minute);
        } else if(minute>=10 && minute<=60){
            builder.append(minute);
        }
        builder.append(":");
        if(second>=0 && second<=9){
            builder.append("0").append(second);
        } else if(second>=10 && second<=60){
            builder.append(second);
        }
        return  builder.toString();
    }

}
