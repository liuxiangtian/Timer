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

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.ImageUtil;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity implements TrackAdaptor.OnItemClickListener {

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private TrackAdaptor trackAdaptor;
    private Intent intent;

    private Palette.PaletteAsyncListener paletteAsyncLister = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            if (swatch != null) {
                int bgColor = swatch.getRgb();
                int textColor = swatch.getTitleTextColor();
                collapsingToolbar.setContentScrimColor(bgColor);
                trackAdaptor.setTextColor(textColor);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PrefsUtil.getThemeType()==PrefsUtil.THEME_DARK){
            setTheme(R.style.AppThemeDark_FullScreen);
        } else {
            setTheme(R.style.AppThemeLight_FullScreen);
        }
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        trackAdaptor = new TrackAdaptor(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(trackAdaptor);
        trackAdaptor.setOnItemClickListener(this);
        recyclerView.addItemDecoration(new DivideItemDecoration(1));

        initViews();
    }

    private void initViews() {
        String type = intent.getStringExtra(ConstantsUtil.DETAIL_TYPE);
        if (ConstantsUtil.DETAIL_TYPE_PLAYLIST.equals(type)) {
            long playlistId = intent.getLongExtra(ConstantsUtil.DETAIL_PLAYLIST_ID, -1);
            LoadUtil.loadTracksByPlaylistId(this, playlistId, trackAdaptor);
            ImageUtil.loadBitmapByPlaylistId(albumArt, playlistId, paletteAsyncLister);
        } else if (ConstantsUtil.DETAIL_TYPE_ALBUM.equals(type)) {
            long albumId = intent.getLongExtra(ConstantsUtil.DETAIL_ALBUM_ID, -1);
            LoadUtil.loadTracksByAlbumId(this, albumId, trackAdaptor);
            ImageUtil.loadBlurBitmap(albumArt, albumId);
        } else if (ConstantsUtil.DETAIL_TYPE_ARTIST.equals(type)) {
            long artistId = intent.getLongExtra(ConstantsUtil.DETAIL_ARTIST_ID, -1);
            LoadUtil.loadTracksByArtistId(this, artistId, trackAdaptor);
            ImageUtil.loadBitmapBtArtistId(albumArt, artistId, paletteAsyncLister);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(Track item, int position, long[] ids) {
        PlayUtil.playTracks(this, ids, item.getId(), position);
        trackAdaptor.notifyItem(position, true);
    }

    @Override
    protected void onMetaPlay() {
        super.onMetaPlay();
        PlayUtil.responsePlay(this, trackAdaptor);
    }

}
