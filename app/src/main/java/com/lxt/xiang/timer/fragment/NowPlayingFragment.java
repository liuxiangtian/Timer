package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.OnPlayStateChangeListener;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.ImageUtil;
import com.lxt.xiang.timer.util.PlayUtil;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NowPlayingFragment extends Fragment implements OnPlayStateChangeListener {

    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.song_title)
    TextView songTitle;
    @Bind(R.id.song_artist)
    TextView songArtist;
    @Bind(R.id.song_progress)
    SeekBar songProgress;
    @Bind(R.id.song_album)
    TextView songAlbum;
    @Bind(R.id.song_elapsed_time)
    TextView songElapsedTime;
    @Bind(R.id.song_duration)
    TextView songDuration;
    @Bind(R.id.previous)
    MaterialIconView previous;
    @Bind(R.id.playpausewrapper)
    View playpausewrapper;
    @Bind(R.id.next)
    MaterialIconView next;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.shuffle)
    FloatingActionButton shuffle;
    TrackAdaptor trackAdaptor;

    private Handler handler;
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            if (baseActivity == null) {
                return;
            }
            ITimerInterface iTimerInterface = baseActivity.iTimerService;
            if (iTimerInterface == null) return;
            try {
                long position = iTimerInterface.getSeekPosition();
                Track track = iTimerInterface.getCurrentTrack();
                long duration = track.getDuration();
                songProgress.setProgress((int) (position*ConstantsUtil.PROCESS_MAX/duration));
                songElapsedTime.setText(PlayUtil.durationToString(position));
                if(iTimerInterface.isPlaying()){
                    handler.postDelayed(updateRunnable, 1000);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener togglePlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.togglePlay(baseActivity);
        }
    };
    private View.OnClickListener playNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.playNext(baseActivity);
        }
    };
    private View.OnClickListener playPrevListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.playPrev(baseActivity);
        }
    };
    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.seek(baseActivity, progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public static NowPlayingFragment newInstance() {
        return new NowPlayingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, root);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trackAdaptor = new TrackAdaptor(null);
        recyclerView.setAdapter(trackAdaptor);

        songProgress.setMax(ConstantsUtil.PROCESS_MAX);
//        songProgress.setOnSeekBarChangeListener(seekListener);
        playpausewrapper.setOnClickListener(togglePlayListener);
        next.setOnClickListener(playNextListener);
        previous.setOnClickListener(playPrevListener);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) {
            return;
        }
        baseActivity.addOnPlayStateChangeListener(this);
        update();
    }

    private void update() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) {
            return;
        }
        ITimerInterface iTimerInterface = baseActivity.iTimerService;
        if (iTimerInterface == null) return;
        try {
            Track track = iTimerInterface.getCurrentTrack();
            if (track==null) return;
            songTitle.setText(track.getTitle());
            songArtist.setText(track.getArtist());
            songAlbum.setText(track.getAlbum());
            ImageUtil.loadBitmap(albumArt, track.getAlbumId());
            long duration = track.getDuration();
            long seekPosition = iTimerInterface.getSeekPosition();
            songDuration.setText(PlayUtil.durationToString(duration));
            songElapsedTime.setText(PlayUtil.durationToString(seekPosition));
            trackAdaptor.replaceData(iTimerInterface.getQueues());
            handler.post(updateRunnable);
            songProgress.setProgress((int) (seekPosition*ConstantsUtil.PROCESS_MAX/duration));
            int position = iTimerInterface.getCurrentPosition();
            trackAdaptor.notifyItem(position, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) {
            return;
        }
        baseActivity.removeOnPlayStateChangeListener(this);
    }

    @Override
    public void onMetaChange() {

    }

    @Override
    public void onMetaPlay() {
        update();
    }

    @Override
    public void onMetaPause() {
    }

    @Override
    public void onMetaStop() {

    }
}