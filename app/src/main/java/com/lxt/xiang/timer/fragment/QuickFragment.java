package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.listener.OnPlayStateChangeListener;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.ImageUtil;
import com.lxt.xiang.timer.util.PlayUtil;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuickFragment extends Fragment implements OnPlayStateChangeListener {

    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.text_title)
    TextView titleText;
    @Bind(R.id.text_artist)
    TextView artistText;
    @Bind(R.id.play_icon)
    MaterialIconView playIcon;
    @Bind(R.id.next_icon)
    MaterialIconView nextIcon;
    @Bind(R.id.prev_icon)
    MaterialIconView prevIcon;
    @Bind(R.id.song_progress_fast)
    ProgressBar progressBarFast;
    @Bind(R.id.song_progress)
    SeekBar progressBar;
    @Bind(R.id.album_art_fast) ImageView albumArtFast;
    @Bind(R.id.text_title_fast) TextView titleFastText;
    @Bind(R.id.text_artist_fast) TextView artistFastText;
    @Bind(R.id.icon_fast) MaterialIconView iconFast;

    Handler handler = new Handler();
    private View.OnClickListener togglePlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.togglePlay(baseActivity);
        }
    };
    private View.OnClickListener nextPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            PlayUtil.playNext(baseActivity);
        }
    };
    private View.OnClickListener prevPlayListener = new View.OnClickListener() {
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

    public static QuickFragment newInstance() {
        return new QuickFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
                Track track = iTimerInterface.getCurrentTrack();
                long position = iTimerInterface.getSeekPosition();
                long duration = track.getDuration();
                progressBar.setProgress((int) (position*ConstantsUtil.PROCESS_MAX/duration));
                progressBarFast.setProgress((int) (position*ConstantsUtil.PROCESS_MAX/duration));
                if(iTimerInterface.isPlaying()){
                    handler.postDelayed(updateRunnable, 1000);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quick_control, container, false);
        ButterKnife.bind(this,root);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.addOnPlayStateChangeListener(this);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playIcon.setOnClickListener(togglePlayListener);
        iconFast.setOnClickListener(togglePlayListener);
        nextIcon.setOnClickListener(nextPlayListener);
        prevIcon.setOnClickListener(prevPlayListener);
        progressBar.setOnSeekBarChangeListener(seekListener);
        progressBar.setMax(ConstantsUtil.PROCESS_MAX);
        progressBarFast.setMax(ConstantsUtil.PROCESS_MAX);
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
            titleFastText.setText(track.getTitle());
            titleText.setText(track.getTitle());
            artistFastText.setText(track.getArtist());
            artistText.setText(track.getArtist());
            if(iTimerInterface.isPlaying()){
                iconFast.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
                playIcon.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
            } else {
                iconFast.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                playIcon.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
            }
            ImageUtil.loadBlurBitmap(albumArtFast, track.getAlbumId());
            ImageUtil.loadBitmap(albumArt, track.getAlbumId());
            long duration = track.getDuration();
            long seekPosition = iTimerInterface.getSeekPosition();
            progressBar.setProgress((int) (seekPosition*ConstantsUtil.PROCESS_MAX/duration));
            progressBarFast.setProgress((int) (seekPosition*ConstantsUtil.PROCESS_MAX/duration));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.removeOnPlayStateChangeListener(this);
        }
    }

    @Override
    public void onMetaChange() {
        update();
    }

    @Override
    public void onMetaPlay() {
        update();
        handler.post(updateRunnable);
    }

    @Override
    public void onMetaPause() {

    }

    @Override
    public void onMetaStop() {

    }
}
