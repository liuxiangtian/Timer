package com.lxt.xiang.timer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.fragment.MainFragment;
import com.lxt.xiang.timer.fragment.NowPlayingFragment;
import com.lxt.xiang.timer.fragment.PlaylistFragment;
import com.lxt.xiang.timer.fragment.QueueFragment;
import com.lxt.xiang.timer.fragment.QuickFragment;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.ImageUtil;
import com.lxt.xiang.timer.util.NaviUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String NAVI_LIBRARY = "NAVI_LIBRARY";
    public static final String NAVI_PLAYLIST = "NAVI_PLAYLIST";
    public static final String NAVI_PLAY_QUEUE = "NAVI_PLAY_QUEUE";
    public static final String NAVI_NOW_PLAYING = "NAVI_NOW_PLAYING";
    private static final String NAVIGATION = "NAVIGATION";

    @Bind(R.id.sliding_panel)
    SlidingUpPanelLayout slidingPanel;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navView;

    ImageView headerImage;
    TextView headerTrack;
    TextView headerArtist;

    private Handler handler;
    private String navigation;
    private Map<String, Runnable> naviRunnables;
    private String themeType;

    private Runnable toLibrary = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, MainFragment.newInstance())
                    .commit();
        }
    };

    private Runnable toPlaylist = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, PlaylistFragment.newInstance())
                    .addToBackStack("PLAYLIST")
                    .commit();
        }
    };
    private Runnable toPlayQueue = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, QueueFragment.newInstance())
                        .addToBackStack("PLAYQUEUE")
                    .commit();
        }
    };
    private Runnable toNowPlaying = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, NowPlayingFragment.newInstance())
                    .addToBackStack("PLAYQUEUE")
                    .commit();
            slidingPanel.setPanelHeight(0);
        }
    };
    private Runnable toQuickControl = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fast_frame_layout, QuickFragment.newInstance())
                    .commit();
        }
    };

    private SlidingUpPanelLayout.PanelSlideListener panelSlideListener
            = new SlidingUpPanelLayout.SimplePanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            panel.findViewById(R.id.fast_control).setAlpha(1 - slideOffset);
            panel.findViewById(R.id.content_frame_layout).setAlpha(slideOffset);
        }

        @Override
        public void onPanelCollapsed(View panel) {
//            panel.findViewById(R.id.content_frame_layout).setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPanelExpanded(View panel) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        themeType = PrefsUtil.getThemeType();
        if (themeType == PrefsUtil.THEME_DARK) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupViews(this);
        setupData(this);
    }

    private void setupData(final Context context) {
        handler = new Handler();
        naviRunnables = new HashMap<>();
        naviRunnables.put(NAVI_LIBRARY, toLibrary);
        naviRunnables.put(NAVI_PLAYLIST, toPlaylist);
        naviRunnables.put(NAVI_PLAY_QUEUE, toPlayQueue);
        naviRunnables.put(NAVI_NOW_PLAYING, toNowPlaying);
        Intent intent = getIntent();
        if (intent != null) {
            navigation = intent.getStringExtra(NAVIGATION);
            if (navigation == null) {
                navigation = NAVI_LIBRARY;
            }
            handler.post(naviRunnables.get(navigation));
        }
        handler.post(toQuickControl);
    }

    private void setupViews(final Context context) {
        View header = navView.getHeaderView(0);
        headerImage = (ImageView) header.findViewById(R.id.header_imageView);
        headerTrack = (TextView) header.findViewById(R.id.header_track);
        headerArtist = (TextView) header.findViewById(R.id.header_artist);

        slidingPanel.collapsePanel();
        slidingPanel.setPanelSlideListener(panelSlideListener);

        navView.setNavigationItemSelectedListener(this);
        Menu menu = navView.getMenu();
        if (themeType == PrefsUtil.THEME_LIGHT) {
            menu.findItem(R.id.nav_library).setIcon(R.drawable.ic_album_white_18dp);
            menu.findItem(R.id.nav_playlist).setIcon(R.drawable.ic_explicit_white_18dp);
            menu.findItem(R.id.nav_play_queue).setIcon(R.drawable.ic_queue_music_white_18dp);
            menu.findItem(R.id.nav_now_playing).setIcon(R.drawable.ic_equalizer_white_18dp);
            menu.findItem(R.id.nav_settings).setIcon(R.drawable.ic_settings_white_18dp);
            menu.findItem(R.id.nav_about).setIcon(R.drawable.ic_apps_white_18dp);
            menu.findItem(R.id.nav_feedback).setIcon(R.drawable.ic_help_white_18dp);
        } else {
            menu.findItem(R.id.nav_library).setIcon(R.drawable.ic_album_black_18dp);
            menu.findItem(R.id.nav_playlist).setIcon(R.drawable.ic_explicit_black_18dp);
            menu.findItem(R.id.nav_play_queue).setIcon(R.drawable.ic_queue_music_black_18dp);
            menu.findItem(R.id.nav_now_playing).setIcon(R.drawable.ic_equalizer_black_18dp);
            menu.findItem(R.id.nav_settings).setIcon(R.drawable.ic_settings_black_18dp);
            menu.findItem(R.id.nav_about).setIcon(R.drawable.ic_apps_black_18dp);
            menu.findItem(R.id.nav_feedback).setIcon(R.drawable.ic_help_black_18dp);
        }
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onMetaChange();
    }

    @Override
    protected void onMetaChange() {
        super.onMetaChange();
        if (iTimerService == null) return;
        try {
            Track track = iTimerService.getCurrentTrack();
            updateHeader(track);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMetaPlay() {
        super.onMetaPlay();
        onMetaChange();
    }

    private void updateHeader(Track track) {
        if (track == null) {
            track = TrackLoader.loadRandomTrack(this);
        }
        headerTrack.setText(track.getTitle());
        headerArtist.setText(track.getArtist());
        ImageUtil.loadBitmap(headerImage, track.getAlbumId());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        } else if (id == R.id.menu_settings) {
            NaviUtil.naviToSettingsActivity(this);
        } else if (id == R.id.menu_search) {
            NaviUtil.naviToSearchActivity(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setCheckable(true);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_library:
                navigation = NAVI_LIBRARY;
                handler.post(naviRunnables.get(navigation));
                break;
            case R.id.nav_playlist:
                navigation = NAVI_PLAYLIST;
                handler.post(naviRunnables.get(navigation));
                break;
            case R.id.nav_play_queue:
                navigation = NAVI_PLAY_QUEUE;
                handler.post(naviRunnables.get(navigation));
                break;
            case R.id.nav_now_playing:
                navigation = NAVI_NOW_PLAYING;
                handler.post(naviRunnables.get(navigation));
                break;
            case R.id.nav_settings:
                NaviUtil.naviToSettingsActivity(this);
                break;
            case R.id.nav_about:
                NaviUtil.naviToAbout(getSupportFragmentManager(), "ABOUT");
                break;
            case R.id.nav_feedback:
                NaviUtil.naviToFeedBack(this);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
