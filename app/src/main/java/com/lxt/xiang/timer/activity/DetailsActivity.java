package com.lxt.xiang.timer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity implements TrackAdaptor.OnItemClickListener {

    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    TextView textTitle;
    private TrackAdaptor trackAdaptor;
    private CollapsingToolbarLayout collapsingToolbar;

    private String type;
    private Intent intent;

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
//        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, android.R.anim.slide_in_left);
//        recyclerView.setLayoutAnimation(controller);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
            }
        }, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayUtil.registerPlayObserver(this, playObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayUtil.unRegisterPlayObserver(this, playObserver);
    }

    private void initViews() {
        if (ConstantsUtil.DETAIL_TYPE_PLAYLIST.equals(type)) {
            initPlayList();
        } else if (ConstantsUtil.DETAIL_TYPE_ALBUM.equals(type)) {
            initAlbum();
        } else if (ConstantsUtil.DETAIL_TYPE_ARTIST.equals(type)) {
            initArtist();
        }
    }

    private void initArtist() {
        setupToolbar();
        long artistId = intent.getLongExtra(ConstantsUtil.DETAIL_ARTIST_ID, -1);
        String artistName = intent.getStringExtra(ConstantsUtil.DETAIL_ARTIST_NAME);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(artistName);
        LoadUtil.loadTracksByArtist(this, artistId, trackAdaptor);
        BitmapUtil.loadBitmapByArtistId(albumArt, artistId, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    int bgColor = swatch.getRgb();
                    collapsingToolbar.setContentScrimColor(bgColor);
                }
            }
        });
    }

    private void initAlbum() {
        setupToolbar();
        long albumId = intent.getLongExtra(ConstantsUtil.DETAIL_ALBUM_ID, -1);
        String albumName = intent.getStringExtra(ConstantsUtil.DETAIL_ALBUM_NAME);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(albumName);
        LoadUtil.loadTracksByAlbum(this, albumId, trackAdaptor);
        BitmapUtil.loadBitmap(albumArt, albumId, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    int bgColor = swatch.getRgb();
                collapsingToolbar.setContentScrimColor(bgColor);
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initPlayList() {
        long playlistId = intent.getLongExtra(ConstantsUtil.DETAIL_PLAYLIST_ID, -1);
        String playlistName = intent.getStringExtra(ConstantsUtil.DETAIL_PLAYLIST_NAME);
        textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText(playlistName);
        LoadUtil.loadTracksByPlaylist(this, playlistId, trackAdaptor);
        BitmapUtil.loadBlurBitmapByPlaylist(albumArt, playlistId, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    int textColor = swatch.getTitleTextColor();
                    trackAdaptor.setTextColor(textColor);
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
        trackAdaptor.notifyItem(position, true);
    }


    private PlayObserver playObserver = new PlayObserver() {

        @Override
        public void onMetaPlay() {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Track track = PlayUtil.getCurrentTrack(iTimerService);
                    trackAdaptor.refreshTrack(track);
                }
            }, 200);
        }
    };

}
