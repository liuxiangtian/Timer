package com.lxt.xiang.timer.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.PlayUtil;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NowPlayingFragment extends Fragment implements TrackAdaptor.OnItemClickListener {

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
    @Bind(R.id.playpause)
    Button playpause;
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
            if (!PlayUtil.checkActivityIsBind(getActivity())) return;
            ITimerInterface iTimerInterface = PlayUtil.getITimerService(getActivity());
            try {
                long position = iTimerInterface.getSeekPosition();
                long duration = iTimerInterface.getDuration();
                long realPosition = Math.min(Math.max(0, position), duration);
                int newProgress = (int) (realPosition * ConstantsUtil.PROCESS_MAX / duration);
                songProgress.setProgress(newProgress);
                songElapsedTime.setText(PlayUtil.durationToString(realPosition));
                songDuration.setText(PlayUtil.durationToString(duration));
                if (iTimerInterface.isPlaying()) {
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
            if (fromUser) {
                PlayUtil.seek(baseActivity, progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private Palette.PaletteAsyncListener palettListener = new Palette.PaletteAsyncListener() {

        @Override
        public void onGenerated(Palette palette) {
            int bgColor = palette.getDominantColor(Color.MAGENTA);
            int contrastColor = BitmapUtil.getContrastColor(bgColor) | 0xff000000;
                songTitle.setTextColor(contrastColor);
                songArtist.setTextColor(contrastColor);
                songDuration.setTextColor(contrastColor);
                songElapsedTime.setTextColor(contrastColor);
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
        toolbar.setTitle("");
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trackAdaptor = new TrackAdaptor(null);
        trackAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(trackAdaptor);

        songProgress.setMax(ConstantsUtil.PROCESS_MAX);
        songProgress.setOnSeekBarChangeListener(seekListener);
        playpausewrapper.setOnClickListener(togglePlayListener);
        playpause.setOnClickListener(togglePlayListener);
        shuffle.setOnClickListener(togglePlayListener);
        next.setOnClickListener(playNextListener);
        previous.setOnClickListener(playPrevListener);

        PlayUtil.registerPlayObserver(getActivity(), playObserver);
        handler.postDelayed(setupViewRunnable, 200);
    }

    private Runnable setupViewRunnable = new Runnable() {

        @Override
        public void run() {
            if (!PlayUtil.checkActivityIsBind(getActivity())) return;
            ITimerInterface iTimerInterface = PlayUtil.getITimerService(getActivity());
            try {
                Track track = iTimerInterface.getCurrentTrack();
                if (track == null) return;
                songTitle.setText(track.getTitle());
                songArtist.setText(track.getArtist());
                songAlbum.setText(track.getAlbum());
                BitmapUtil.loadBlurBitmap(albumArt, track.getAlbumId(), palettListener);
                long duration = iTimerInterface.getDuration();
                songDuration.setText(PlayUtil.durationToString(duration));
                trackAdaptor.replaceData(iTimerInterface.getQueues());
                handler.post(updateRunnable);
                PlayUtil.probePlayState(getActivity(), trackAdaptor);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayUtil.unRegisterPlayObserver(getActivity(), playObserver);
    }

    private PlayObserver playObserver = new PlayObserver() {

        @Override
        public void onMetaChange(Track track) {
            handler.postDelayed(setupViewRunnable, 200);
        }

        @Override
        public void onMetaPlay() {
            handler.postDelayed(updateRunnable, 200);
            PlayUtil.probePlayState(getActivity(), trackAdaptor);
        }

        @Override
        public void onMetaPause() {
            handler.postDelayed(updateRunnable, 200);
            PlayUtil.probePlayState(getActivity(), trackAdaptor);
        }
    };

    @Override
    public void onItemClick(Track item, int position, long[] ids) {
        PlayUtil.playTracks((BaseActivity) getActivity(), ids,item.getId(), position);
    }

}