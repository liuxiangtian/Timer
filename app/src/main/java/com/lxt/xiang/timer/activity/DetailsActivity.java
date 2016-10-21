package com.lxt.xiang.timer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.listener.SimpleTransitionListener;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity implements TrackAdaptor.OnItemClickListener, View.OnClickListener {

    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    TextView textTitle;
    private TrackAdaptor trackAdaptor;
    private CollapsingToolbarLayout collapsingToolbar;

    private String type;
    private Intent intent;
    private FloatingActionButton fab;

    private SimpleTransitionListener enterTransitionListener = new SimpleTransitionListener() {

        @Override
        public void onTransitionStart(Transition transition) {
            recyclerView.setEnabled(false);
        }

        @Override
        public void onTransitionEnd(Transition transition) {
            super.onTransitionEnd(transition);
            recyclerView.setEnabled(true);
            initViews();
        }
    };
    private long playlistId;
    private String playlistName;
    private long albumId;
    private String albumName;
    private long artistId;
    private String artistName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PrefsUtil.getThemeType() == PrefsUtil.THEME_DARK) {
            setTheme(R.style.AppThemeDark_FullScreen);
        } else {
            setTheme(R.style.AppThemeLight_FullScreen);
        }
        intent = getIntent();
        type = intent.getStringExtra(ConstantsUtil.DETAIL_TYPE);
        if (ConstantsUtil.DETAIL_TYPE_PLAYLIST.equals(type)) {
            setContentView(R.layout.activity_details);
        } else if (ConstantsUtil.DETAIL_TYPE_ALBUM.equals(type) || ConstantsUtil.DETAIL_TYPE_ARTIST.equals(type)) {
            setContentView(R.layout.activity_details_album_list);
        }
        ButterKnife.bind(this);

        trackAdaptor = new TrackAdaptor(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(trackAdaptor);
        trackAdaptor.setOnItemClickListener(this);
        recyclerView.addItemDecoration(new DivideItemDecoration(1));
        Animation animation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF,-1,
                TranslateAnimation.RELATIVE_TO_SELF,0,
                TranslateAnimation.RELATIVE_TO_SELF, -1,
                TranslateAnimation.RELATIVE_TO_SELF,0);
        Animation alpha  = new AlphaAnimation(0,1);
        LayoutAnimationController controller = new LayoutAnimationController(alpha,3000);
        recyclerView.setLayoutAnimation(controller);

        setupBackground();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && PrefsUtil.getNeedTransition()) {
            getWindow().getEnterTransition().addListener(enterTransitionListener);
        } else {
            initViews();
        }
    }

    private void setupBackground() {
        if (ConstantsUtil.DETAIL_TYPE_PLAYLIST.equals(type)) {
            initPlayList();
        } else if (ConstantsUtil.DETAIL_TYPE_ALBUM.equals(type)) {
            initAlbum();
        } else if (ConstantsUtil.DETAIL_TYPE_ARTIST.equals(type)) {
            initArtist();
        }
    }

    private void initViews() {
        if (ConstantsUtil.DETAIL_TYPE_PLAYLIST.equals(type)) {
            LoadUtil.loadTracksByPlaylist(this, playlistId, trackAdaptor);
        } else if (ConstantsUtil.DETAIL_TYPE_ALBUM.equals(type)) {
            LoadUtil.loadTracksByAlbum(this, albumId, trackAdaptor);
        } else if (ConstantsUtil.DETAIL_TYPE_ARTIST.equals(type)) {
            LoadUtil.loadTracksByArtist(this, artistId, trackAdaptor);
        }
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                PlayUtil.probePlayState(DetailsActivity.this, trackAdaptor);
            }
        }, 200);
    }

    private void initArtist() {
        setupToolbar();
        artistId = intent.getLongExtra(ConstantsUtil.DETAIL_ARTIST_ID, -1);
        artistName = intent.getStringExtra(ConstantsUtil.DETAIL_ARTIST_NAME);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(artistName);
        BitmapUtil.loadBitmapByArtistId(albumArt, artistId, paletteAsyncListener);
        setToggleFab();
    }

    private void setToggleFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void initAlbum() {
        setupToolbar();
        albumId = intent.getLongExtra(ConstantsUtil.DETAIL_ALBUM_ID, -1);
        albumName = intent.getStringExtra(ConstantsUtil.DETAIL_ALBUM_NAME);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(albumName);
        BitmapUtil.loadBitmap(albumArt, albumId, paletteAsyncListener);
        setToggleFab();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initPlayList() {
        playlistId = intent.getLongExtra(ConstantsUtil.DETAIL_PLAYLIST_ID, -1);
        playlistName = intent.getStringExtra(ConstantsUtil.DETAIL_PLAYLIST_NAME);
        textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText(playlistName);
        BitmapUtil.loadBlurBitmapByPlaylist(albumArt, playlistId, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    int bgColor = swatch.getRgb();
                    int textColor = BitmapUtil.getContrastColor(bgColor) & 0xff000000;
                    trackAdaptor.setTextColor(textColor);
                    textTitle.setTextColor(textColor);
                } else {
                    int textColor = Color.BLACK;
                    trackAdaptor.setTextColor(textColor);
                    textTitle.setTextColor(textColor);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(Track item, int position, long[] ids) {
        PlayUtil.playTracks(this, ids, item.getId(), position);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        PlayUtil.registerPlayObserver(this, playObserver);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        PlayUtil.unRegisterPlayObserver(this, playObserver);
        super.onServiceDisconnected(name);
    }

    private PlayObserver playObserver = new PlayObserver() {

        @Override
        public void onMetaPlay() {
            PlayUtil.probePlayState(DetailsActivity.this, trackAdaptor);
        }

        @Override
        public void onMetaPause() {
            PlayUtil.probePlayState(DetailsActivity.this, trackAdaptor);
        }
    };

    Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            if (swatch != null) {
                int bgColor = swatch.getRgb();
                int conctastColor = BitmapUtil.getDarkColor(bgColor);
                int textColor = BitmapUtil.getContrastColor(bgColor) & 0xff000000;
                collapsingToolbar.setContentScrimColor(bgColor);
                collapsingToolbar.setStatusBarScrimColor(conctastColor);
                trackAdaptor.setTextColor(textColor);
            }
        }
    };

    @Override
    public void onClick(View v) {
        PlayUtil.togglePlay(DetailsActivity.this);
    }
}
