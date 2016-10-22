package com.lxt.xiang.timer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.fragment.MainFragment;
import com.lxt.xiang.timer.fragment.NowPlayingFragment;
import com.lxt.xiang.timer.fragment.PlaylistFragment;
import com.lxt.xiang.timer.fragment.QueueFragment;
import com.lxt.xiang.timer.fragment.QuickFragment;
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.NavUtil;
import com.lxt.xiang.timer.util.PlayUtil;
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
    public static final String NAVIGATION = "NAVIGATION";

    @Bind(R.id.sliding_panel)
    SlidingUpPanelLayout slidingPanel;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.fast_frame_layout)
    FrameLayout fastFrameLayout;

    ImageView headerImage;
    TextView headerTrack;
    TextView headerArtist;

    private Handler handler;
    private String navigation;
    private int destiny;
    private Map<String, Runnable> naviRunnables;
    private String themeType;

    private Runnable toLibrary = new Runnable() {
        @Override
        public void run() {
            slidingPanel.setPanelHeight(56*destiny);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("MainFragment");
            if(fragment==null){
                fragment = MainFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, fragment, "MainFragment")
                    .commit();
        }
    };

    private Runnable toPlaylist = new Runnable() {
        @Override
        public void run() {
            slidingPanel.setPanelHeight(56*destiny);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("PLAYLIST");
            if(fragment==null){
                fragment = PlaylistFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, fragment, "PLAYLIST")
                    .commit();
        }
    };

    private Runnable toPlayQueue = new Runnable() {
        @Override
        public void run() {
            slidingPanel.setPanelHeight(56*destiny);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("PLAYQUEUE");
            if(fragment==null){
                fragment = QueueFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout,fragment, "PLAYQUEUE")
                    .commit();
        }
    };

    private Runnable toNowPlaying = new Runnable() {
        @Override
        public void run() {
            slidingPanel.setPanelHeight(0*destiny);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("NowPlayingFragment");
            if(fragment==null){
                fragment = NowPlayingFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, fragment, "NowPlayingFragment")
                    .addToBackStack("NowPlayingFragment")
                    .commit();
        }
    };

    private Runnable toQuickControl = new Runnable() {
        @Override
        public void run() {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("QuickFragment");
            if(fragment==null){
                fragment = QuickFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fast_frame_layout, fragment, "QuickFragment")
                    .commit();
        }
    };

    private SlidingUpPanelLayout.PanelSlideListener panelSlideListener
            = new SlidingUpPanelLayout.SimplePanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            panel.findViewById(R.id.fast_control).setVisibility(View.VISIBLE);
            panel.findViewById(R.id.content_frame_layout).setVisibility(View.VISIBLE);
            panel.findViewById(R.id.fast_control).setAlpha(1 - slideOffset);
            panel.findViewById(R.id.content_frame_layout).setAlpha(slideOffset);
        }

        @Override
        public void onPanelCollapsed(View panel) {
            panel.findViewById(R.id.content_frame_layout).setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPanelExpanded(View panel) {
            panel.findViewById(R.id.fast_control).setVisibility(View.INVISIBLE);
        }
    };

    private PlayObserver playStateObserver = new PlayObserver(){

        @Override
        public void onMetaChange(Track track) {
            headerTrack.setText(track.getTitle());
            headerArtist.setText(track.getArtist());
            BitmapUtil.loadBitmap(headerImage, track.getAlbumId(), mPaletteAsyncListener);
        }
    };

    Palette.PaletteAsyncListener mPaletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            int bgColor = palette.getDominantColor(Color.WHITE);
            int textColor = BitmapUtil.getContrastColor(bgColor) & 0xff000000;
            headerTrack.setTextColor(textColor);
            headerArtist.setTextColor(textColor);
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
        handler = new Handler();
        setupViews();
        setupData();
    }

    private void setupData() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        destiny = (int) metrics.density;

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

    private void setupViews() {
        View header = navView.getHeaderView(0);
        headerImage = (ImageView) header.findViewById(R.id.header_imageView);
        headerTrack = (TextView) header.findViewById(R.id.header_track);
        headerArtist = (TextView) header.findViewById(R.id.header_artist);

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
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        PlayUtil.registerPlayObserver(iTimerService, playStateObserver);
        try {
            iTimerService.init();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        PlayUtil.unRegisterPlayObserver(iTimerService, playStateObserver);
        super.onServiceDisconnected(name);
    }

    @Override
    public void onBackPressed() {
        if(navigation==NAVI_NOW_PLAYING){
            slidingPanel.setPanelHeight(56*destiny);
        }
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
            NavUtil.navToSettingsActivity(this);
        } else if (id == R.id.menu_search) {
            NavUtil.navToSearchActivity(this);
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
                NavUtil.navToSettingsActivity(this);
                break;
            case R.id.nav_about:
                NavUtil.navToAbout(getSupportFragmentManager(), "ABOUT");
                break;
            case R.id.nav_feedback:
                NavUtil.navToFeedBack(this);
                break;
            case R.id.nav_quit:
                finish();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
