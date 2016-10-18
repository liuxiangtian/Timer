package com.lxt.xiang.timer.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.lxt.xiang.timer.BaseApplication;
import com.lxt.xiang.timer.loader.AlbumLoader;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.loader.TrackLoader;

public class PrefsUtil {

    public static final String SHARED_PREFERENCES_NAME = "PREFS_TIMER";

    public static final String KEY_THEME_TYPE = "KEY_THEME_TYPE";
    public static final String THEME_DARK = "THEME_DARK";
    public static final String THEME_LIGHT = "THEME_LIGHT";

    public static final String KEY_NEED_FULL_SCREEN = "KEY_NEED_FULL_SCREEN";
    public static final String KEY_POSITION_MAIN_FRAGMENT = "KEY_POSITION_MAIN_FRAGMENT";
    public static final String KEY_LIBRARY_TRACK_SORT = "KEY_LIBRARY_TRACK_SORT";
    private static final String KEY_LIBRARY_ALBUM_SORT = "KEY_LIBRARY_ALBUM_SORT";
    private static final String KEY_LIBRARY_ARTIST_SORT = "KEY_LIBRARY_ARTIST_SORT";
    private static final String KEY_PLAY_POSITION = "KEY_PLAY_POSITION";
    private static final String KEY_NEED_TRANSITION = "KEY_NEED_TRANSITION";

    public static SharedPreferences getSharedPreferences() {
        return BaseApplication.getINSTANCE().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public static String getThemeType() {
        return getSharedPreferences().getString(KEY_THEME_TYPE, THEME_LIGHT);
    }

    public static void setThemeType(String themeType) {
        getEditor().putString(KEY_THEME_TYPE, themeType).apply();
    }


    public static boolean needFullScreen() {
        return getSharedPreferences().getBoolean(KEY_NEED_FULL_SCREEN, true);
    }

    public static void setNeedFullScreen(boolean needFullScreen) {
        getEditor().putBoolean(KEY_NEED_FULL_SCREEN, needFullScreen).apply();
    }


    public static int getMainFragmentPosition() {
        return getSharedPreferences().getInt(KEY_POSITION_MAIN_FRAGMENT, 0);
    }

    public static void setMainFragmentPosition(int position) {
        getEditor().putInt(KEY_POSITION_MAIN_FRAGMENT, position).apply();
    }

    public static String getLibraryTrackSort() {
        return getSharedPreferences().getString(KEY_LIBRARY_TRACK_SORT, TrackLoader.SORT_TITLE);
    }


    public static void setLibraryTrackSort(final String sort) {
        getEditor().putString(KEY_LIBRARY_TRACK_SORT, sort).apply();
    }

    public static String getLibraryAlbumSort() {
        return getSharedPreferences().getString(KEY_LIBRARY_ALBUM_SORT, AlbumLoader.SORT_ALBUM);
    }

    public static void setLibraryAlbumSort(String albumSort) {
        getEditor().putString(KEY_LIBRARY_ALBUM_SORT, albumSort).apply();
    }

    public static String getLibraryArtistSort() {
        return getSharedPreferences().getString(KEY_LIBRARY_ARTIST_SORT, ArtistLoader.SORT_ARTIST);
    }

    public static void setLibraryArtistSort(String artistSort) {
        getEditor().putString(KEY_LIBRARY_ARTIST_SORT, artistSort).apply();
    }

    public static int getQueuePosition() {
        return getSharedPreferences().getInt(KEY_PLAY_POSITION, 0);
    }

    public static void setQueuePosition(int currentPosition) {
        getEditor().putInt(KEY_PLAY_POSITION, currentPosition).apply();
    }

    public static void setNeedTransition(boolean need) {
        getEditor().putBoolean(KEY_NEED_TRANSITION, need).apply();
    }

    public static boolean getNeedTransition() {
        return getSharedPreferences().getBoolean(KEY_NEED_TRANSITION, true);
    }
}
