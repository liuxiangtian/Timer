package com.lxt.xiang.timer.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lxt.xiang.timer.BaseApplication;
import com.lxt.xiang.timer.model.Playlist;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.provider.LastPlayStore;
import com.lxt.xiang.timer.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {

    private static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME
    };

    public static Playlist LAST_ADD = new Playlist(-1, "LAST ADD", 0);
    public static Playlist LAST_PLAY = new Playlist(-2, "LAST_PLAY", 0);
    public static Playlist TOP_TRACK = new Playlist(-3, "TOP_TRACK", 0);

    static {
        Context context = BaseApplication.getINSTANCE();
        int nums = TrackLoader.getLastAddNums(context);
        nums = Math.min(ConstantsUtil.LAST_ADD_TRACK_NUMS, nums);
        LAST_ADD.setSongCount(nums);
        nums = LastPlayStore.getInstance(context).queryNums(context);
        LAST_PLAY.setSongCount(nums);
        TOP_TRACK.setSongCount(nums);
    }

    public static Cursor makeCursor(final Context context) {
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                null, null, null, null);
    }

    public static List<Playlist> loadPlaylists(final Context context) {
        Cursor cursor = makeCursor(context);
        List<Playlist> playlists = new ArrayList<>();
        playlists.add(LAST_ADD);
        playlists.add(TOP_TRACK);
        playlists.add(LAST_PLAY);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String playlist = getPlaylistName(cursor.getString(1));
                int num = getPlaylistNums(context, id);
                if (num != 0) {
                    playlists.add(new Playlist(id, playlist, num));
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return playlists;
    }

    private static String getPlaylistName(String fullname) {
        if (TextUtils.isEmpty(fullname)) return "";
        String[] names = fullname.split("/");
        int count = names.length;
        if (count > 0) return names[count - 1];
        return fullname;
    }

    public static long getAlbumIdByPlaylistId(final Context context, long id) {
        if (id == -1) {
//            return TrackLoader.getAlbumIdFromLastAdd(context);
            return 2;
        } else if(id == -2 || id == -3){
            long trackId = LastPlayStore.getInstance(context).queryRandomId(context);
            Track track = TrackLoader.loadTrack(context, trackId);
            if (track != null) {
                return track.getAlbumId();
            }
            return -1;
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", id),
                new String[]{"album_id"}, null, null, null);
        long albumId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            albumId = cursor.getLong(0);
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return albumId;
    }

    private static int getPlaylistNums(final Context context, long id) {
        if (id == -1) {
            return LAST_ADD.getSongCount();
        } else if(id == -2){
            return LAST_PLAY.getSongCount();
        }else if(id == -3){
            return TOP_TRACK.getSongCount();
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", id),
                new String[]{"_id"}, null, null, null);
        int num = 0;
        if (cursor != null && cursor.moveToFirst()) {
            num = cursor.getCount();
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return num;
    }

}
