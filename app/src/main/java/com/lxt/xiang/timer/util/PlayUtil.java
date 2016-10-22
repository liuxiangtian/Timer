package com.lxt.xiang.timer.util;


import android.app.Activity;
import android.os.RemoteException;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.model.Track;

import org.joda.time.Duration;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class PlayUtil {

    public static void playTracks(final BaseActivity baseActivity, long[] ids, long id, int position) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            iTimerService.play(ids, id, position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void togglePlay(final BaseActivity baseActivity) {
        if (baseActivity == null) return;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            if (iTimerService.isPlaying()) {
                iTimerService.pause();
            } else {
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
            long duration = iTimerService.getDuration();
            iTimerService.seek(progress * duration / ConstantsUtil.PROCESS_MAX);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String durationToString(long duration) {
        Duration d = new Duration(duration);
        Minutes minutes = d.toStandardMinutes();
        Seconds seconds = d.toStandardSeconds();
        int minute = minutes.getMinutes();
        int second = seconds.getSeconds() % 60;
        StringBuilder builder = new StringBuilder();
        if (minute >= 0 && minute <= 9) {
            builder.append("0").append(minute);
        } else if (minute >= 10 && minute <= 60) {
            builder.append(minute);
        }
        builder.append(":");
        if (second >= 0 && second <= 9) {
            builder.append("0").append(second);
        } else if (second >= 10 && second <= 60) {
            builder.append(second);
        }
        return builder.toString();
    }

    public static boolean checkActivityIsBind(Activity activity) {
        BaseActivity baseActivity = (BaseActivity) activity;
        if (baseActivity == null) return false;
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return false;
        return true;
    }

    public static ITimerInterface getITimerService(Activity activity) {
        BaseActivity baseActivity = (BaseActivity) activity;
        return baseActivity.iTimerService;
    }
    public static void registerPlayObserver(ITimerInterface iTimerService, PlayObserver playStateObserver) {
        if (iTimerService == null || playStateObserver == null) return;
        try {
            iTimerService.registerPlayObserver(playStateObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void registerPlayObserver(Activity activity, PlayObserver playStateObserver) {
        BaseActivity baseActivity = (BaseActivity) activity;
        if (baseActivity == null) return;
        ITimerInterface iTimerInterface = baseActivity.iTimerService;
        registerPlayObserver(iTimerInterface, playStateObserver);
    }

    public static void unRegisterPlayObserver(Activity activity, PlayObserver playStateObserver) {
        BaseActivity baseActivity = (BaseActivity) activity;
        if (baseActivity == null) return;
        ITimerInterface iTimerInterface = baseActivity.iTimerService;
        unRegisterPlayObserver(iTimerInterface, playStateObserver);
    }

    public static void unRegisterPlayObserver(ITimerInterface iTimerService, PlayObserver playStateObserver) {
        if (iTimerService == null || playStateObserver == null) return;
        try {
            iTimerService.unRegisterPlayObserver(playStateObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Track getCurrentTrack(ITimerInterface iTimerService) {
        if (iTimerService == null) return null;
        try {
            return iTimerService.getCurrentTrack();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int probePlayState(Activity activity, TrackAdaptor trackAdaptor) {
        if(!checkActivityIsBind(activity)) return 0;
        ITimerInterface iTimerService = getITimerService(activity);
        try {
            Track track = iTimerService.getCurrentTrack();
            boolean isPlaying = iTimerService.isPlaying();
            trackAdaptor.refreshTrack(track, isPlaying);
            int position = iTimerService.getCurrentPosition();
            return Math.max(0, Math.min(trackAdaptor.getItemCount(), position));
        } catch (RemoteException e) {
            return 0;
        }
    }

}
