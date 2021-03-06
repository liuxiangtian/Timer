package com.lxt.xiang.timer.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
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
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.PlayUtil;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuickFragment extends Fragment {

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
    @Bind(R.id.album_art_fast)
    ImageView albumArtFast;
    @Bind(R.id.text_title_fast)
    TextView titleFastText;
    @Bind(R.id.text_artist_fast)
    TextView artistFastText;
    @Bind(R.id.icon_fast)
    MaterialIconView iconFast;

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
            if(fromUser){
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

    private Runnable refreshViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (!PlayUtil.checkActivityIsBind(getActivity())) return;
            ITimerInterface iTimerInterface = PlayUtil.getITimerService(getActivity());
            try {
                Track track = iTimerInterface.getCurrentTrack();
                if (track == null) return;
                titleFastText.setText(track.getTitle());
                titleText.setText(track.getTitle());
                artistFastText.setText(track.getArtist());
                artistText.setText(track.getArtist());
                if (iTimerInterface.isPlaying()) {
                    iconFast.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
                    playIcon.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
                } else {
                    iconFast.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                    playIcon.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                }
                BitmapUtil.loadBitmap(albumArtFast, track.getAlbumId());
                BitmapUtil.loadBlurBitmap(albumArt, track.getAlbumId(), new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int bgColor = palette.getDominantColor(Color.WHITE);
                        int textColor = BitmapUtil.getContrastColor(bgColor);
                            titleText.setTextColor(textColor);
                            artistText.setTextColor(textColor);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

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
                progressBar.setProgress(newProgress);
                progressBarFast.setProgress(newProgress);
                if (iTimerInterface.isPlaying()) {
                    iconFast.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
                    playIcon.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
                } else {
                    iconFast.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                    playIcon.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                }
                if (iTimerInterface.isPlaying()) {
                    handler.postDelayed(updateRunnable, 1000);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private PlayObserver playObserver = new PlayObserver() {

        @Override
        public void onMetaChange(Track track) {
            handler.postDelayed(refreshViewRunnable, 200);
            handler.postDelayed(updateRunnable, 200);
        }

        @Override
        public void onMetaPlay() {
            handler.postDelayed(updateRunnable, 200);
        }

        @Override
        public void onMetaPause() {
            handler.postDelayed(refreshViewRunnable, 200);
        }
    };

    public static QuickFragment newInstance() {
        return new QuickFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quick_control, container, false);
        ButterKnife.bind(this, root);
        PlayUtil.registerPlayObserver(getActivity(), playObserver);
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
        handler.postDelayed(updateRunnable, 200);
        handler.postDelayed(refreshViewRunnable, 200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayUtil.unRegisterPlayObserver(getActivity(), playObserver);
    }

}
