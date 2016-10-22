package com.lxt.xiang.timer.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.provider.LastPlayStore;
import com.lxt.xiang.timer.provider.PlayQueueStore;
import com.lxt.xiang.timer.util.PrefsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TimerService extends Service {

    private int currentQueuePosition = -1;
    private long currentTrackId = -1;
    private List<Track> mQueues;
    private boolean isMediaPlayerBeUsing;

    private TimerStub mTimerStub;
    private MediaPlayer mediaPlayer;
    private boolean isFirstCompletion = true;
    private boolean isFirstPrepare = true;

    private AudioManager audioManager;
    private LastPlayStore lastPlayStore;
    private List<PlayObserver> playObservers;

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (!isFirstCompletion) {
                mp.reset();
                next();
            }
            isFirstCompletion = false;
        }
    };

    private AudioManager.OnAudioFocusChangeListener onFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (isPlaying()) {
                        pause();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            isMediaPlayerBeUsing = true;
            if (!isFirstPrepare) {
                start();
            }
            isFirstPrepare = false;
            onMetaPrepare();
        }
    };

    private void onMetaPrepare() {
        Log.i("main", "onMetaPrepare: "  );
        for (PlayObserver playObserver : playObservers) {
            playObserver.onPrepare();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimerStub = new TimerStub(this);
        mediaPlayer = new MediaPlayer();
        isFirstCompletion = true;
        lastPlayStore = LastPlayStore.getInstance(this);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        playObservers = new ArrayList<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTimerStub.asBinder();
    }


    private void init() {
        Context context = getApplicationContext();
        mQueues = ensureQueueNotNull(context);
        Log.i("main", "init: " +mQueues.size() + " "+currentTrackId );
        if (mQueues != null && currentTrackId != -1) {
            realPlay(currentTrackId);
        }
    }

    private List<Track> ensureQueueNotNull(Context context) {
        List<Track> tracks = PlayQueueStore.getInstance(context).queryTracks(context);
        currentTrackId = PrefsUtil.getLastPlayTrackId();
        if(tracks==null || tracks.size()==0){
            tracks = TrackLoader.loadTracks(context, TrackLoader.SORT_DATE_ADDED);
            if(tracks==null || tracks.size()==0){
                currentTrackId = -1;
            } else {
                currentTrackId = tracks.get(0).getId();
            }
        }
        return tracks;
    }

    private int findPositionById(long currentTrackId) {
        if (currentTrackId == -1) return 0;
        int ocunt = mQueues.size();
        for (int i = 0; i < ocunt; i++) {
            if (mQueues.get(i).getId() == currentTrackId) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Context context = getApplicationContext();
        PlayQueueStore.getInstance(context).insertItem(context, mQueues);
        PrefsUtil.storeLastPlayTrackId(currentTrackId);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
        playObservers.clear();
        playObservers = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerStub = null;
    }

    private void notifyMetaChange() {
        Track track = findCurrentTrack();
        Log.i("main", "notifyMetaChange: " + track.toString());
        if (track == null) return;
        for (PlayObserver playObserver : playObservers) {
            playObserver.onMetaChange(track);
        }
    }

    private Track findCurrentTrack() {
        if (mQueues == null) return null;
        for (Track track : mQueues) {
            if (track.getId() == currentTrackId) {
                return track;
            }
        }
        return null;
    }

    private void seek(long position) {
        long newPosition = 0;
        if (position < 0) {
            newPosition = 0;
        } else if (position > mediaPlayer.getDuration()) {
            newPosition = mediaPlayer.getDuration();
        } else {
            newPosition = position;
        }
        mediaPlayer.seekTo((int) newPosition);
    }

    private void pause() {
        if (!isMediaPlayerBeUsing) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            notifyMetaPause();
        }
    }

    private void notifyMetaPause() {
        for (PlayObserver playObserver : playObservers) {
            playObserver.onMetaPause();
        }
    }

    private void prev() {
        if (!checkQueueExist()) return;
        if (currentQueuePosition >= 1 && currentQueuePosition <= mQueues.size() - 1) {
            currentQueuePosition = currentQueuePosition - 1;
        } else if (currentQueuePosition == 0) {
            currentQueuePosition = mQueues.size() - 1;
        }
        long id = mQueues.get(currentQueuePosition).getId();
        isFirstCompletion = true;
        realPlay(id);
    }

    private void next() {
        if (!checkQueueExist()) return;
        if (currentQueuePosition >= 0 && currentQueuePosition <= mQueues.size() - 2) {
            currentQueuePosition = currentQueuePosition + 1;
        } else if (currentQueuePosition == mQueues.size() - 1) {
            currentQueuePosition = 0;
        }
        long id = mQueues.get(currentQueuePosition).getId();
        isFirstCompletion = true;
        realPlay(id);
    }

    private boolean checkQueueExist() {
        if (mQueues == null || mQueues.size() == 0) return false;
        return true;
    }

    private void start() {
        int result = audioManager.requestAudioFocus(onFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) return;
        if (!isMediaPlayerBeUsing) return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            notifyMetaPlay();
        }
    }

    private void notifyMetaPlay() {
        for (PlayObserver playObserver : playObservers) {
            playObserver.onMetaPlay();
        }
    }

    private long getSeekPosition() {
        int position = mediaPlayer.getCurrentPosition();
        if (position < 0) {
            position = 0;
        } else if (position > mediaPlayer.getDuration()) {
            position = mediaPlayer.getDuration();
        }
        return position;
    }

    private boolean isPlaying() {
        try {
            return (isMediaPlayerBeUsing && mediaPlayer.isPlaying());
        } catch (IllegalStateException e){
            return false;
        }
    }

    private void play(long[] ids, long id, int position) {
        if (ids == null) return;
        boolean newQueue = false;
        if (mQueues.size() != ids.length) {
            newQueue = true;
        } else {
            for (int i = 0; i < ids.length; i++) {
                if (mQueues.get(i).getId() != ids[i]) {
                    newQueue = true;
                    break;
                }
            }
        }
        if (!newQueue) {
            realPlay(id);
            return;
        }
        mQueues.clear();
        for (int i = 0; i < ids.length; i++) {
            Track track = TrackLoader.getTrack(this, ids[i]);
            if (track != null) {
                mQueues.add(track);
            }
        }
        notifyQueueChange();
        realPlay(id);
    }

    private void notifyQueueChange() {
        for (PlayObserver playObserver : playObservers) {
            playObserver.onQueueChange(mQueues);
        }
    }

    private void realPlay(long id) {
        currentTrackId = id;
        currentQueuePosition = findPositionById(currentTrackId);
        lastPlayStore.insertOrUpdateItem(this, id);
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            if (isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            isMediaPlayerBeUsing = false;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();
            notifyMetaChange();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getQueueSize() {
        if (mQueues == null || mQueues.size() == 0) {
            return -1;
        }
        return mQueues.size();
    }

    private long[] getIds() {
        if (mQueues == null || mQueues.size() == 0) {
            return null;
        }
        int count = mQueues.size();
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = mQueues.get(i).getId();
        }
        return ids;
    }

    private List<Track> getQueues() {
        return mQueues;
    }

    private int getCurrentQueuePosition() {
        return currentQueuePosition;
    }

    private long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return -1;
    }

    private void unRegisterPlayObserver(PlayObserver playObserver) {
        playObservers.remove(playObserver);
    }

    private void registerPlayObserver(PlayObserver playObserver) {
        playObservers.add(playObserver);
    }

    public class TimerStub extends ITimerInterface.Stub {

        private WeakReference<TimerService> weakReference;

        public TimerStub(TimerService timerService) {
            this.weakReference = new WeakReference<>(timerService);
        }

        @Override
        public void init() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) {
                return;
            }
            timerService.init();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) {
                return -1;
            }
            return timerService.getCurrentQueuePosition();
        }

        @Override
        public List<Track> getQueues() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) {
                return null;
            }
            return timerService.getQueues();
        }

        @Override
        public long[] getIds() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) {
                return null;
            }
            return timerService.getIds();
        }

        @Override
        public int getQueueSize() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) {
                return -1;
            }
            return timerService.getQueueSize();
        }

        @Override
        public Track getCurrentTrack() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null || timerService.getQueues() == null || timerService.getQueueSize() <= 0) {
                return null;
            }
            if (currentQueuePosition < 0) {
                currentQueuePosition = 0;
            } else if (currentQueuePosition >= getQueueSize()) {
                currentQueuePosition = getQueueSize() - 1;
            }
            return timerService.getQueues().get(currentQueuePosition);
        }

        @Override
        public long getDuration() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return -1;
            return timerService.getDuration();
        }

        @Override
        public void play(long[] ids, long id, int position) throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.play(ids, id, position);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return false;
            return timerService.isPlaying();
        }

        @Override
        public long getSeekPosition() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return -1;
            return timerService.getSeekPosition();
        }

        @Override
        public void start() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.start();
        }

        @Override
        public void next() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.next();
        }

        @Override
        public void prev() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.prev();
        }

        @Override
        public void pause() throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.pause();
        }

        @Override
        public void seek(long position) throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.seek(position);
        }

        @Override
        public void registerPlayObserver(PlayObserver playObserver) throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.registerPlayObserver(playObserver);
        }

        @Override
        public void unRegisterPlayObserver(PlayObserver playObserver) throws RemoteException {
            TimerService timerService = weakReference.get();
            if (timerService == null) return;
            timerService.unRegisterPlayObserver(playObserver);
        }
    }

}
