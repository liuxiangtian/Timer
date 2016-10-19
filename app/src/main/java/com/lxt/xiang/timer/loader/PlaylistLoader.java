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
            MediaStore.Audio.Playlists.NAME };

    public static Playlist LAST_ADD = new Playlist(-1, "Last Add Tracks", 0);
    public static Playlist LAST_PLAY = new Playlist(-2, "Last Play Tracks", 0);
    public static Playlist TOP_TRACK = new Playlist(-3, "Top Tracks", 0);

    static {
        Context context = BaseApplication.getINSTANCE();
        int count = TrackLoader.getLastAddCount(context);
        count = Math.min(ConstantsUtil.LAST_ADD_TRACK_NUMS, count);
        LAST_ADD.setSongCount(count);
        count = LastPlayStore.getInstance(context).queryNums(context);
        LAST_PLAY.setSongCount(count);
        TOP_TRACK.setSongCount(count);
    }

    public static Cursor makeCursor(final Context context) {
        return context.getContentResolver().query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                PROJECTION, null, null, null);
    }

    public static List<Playlist> loadPlayLists(final Context context) {
        Cursor cursor = makeCursor(context);
        List<Playlist> playLists = new ArrayList<>();
        playLists.add(TOP_TRACK);
        playLists.add(LAST_PLAY);
        playLists.add(LAST_ADD);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String playlist = getPlayListName(cursor.getString(1));
                int num = getPlaylistTrackCount(context, id);
                if (num != 0) {
                    playLists.add(new Playlist(id, playlist, num));
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return playLists;
    }

        public static void closeCursor(Cursor cursor) {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

    private static String getPlayListName(String rawName) {
        if (TextUtils.isEmpty(rawName)) return "";
        String[] names = rawName.split("/");
        int count = names.length;
        if (count > 0) return names[count - 1];
        return rawName;
    }

    private static int getPlaylistTrackCount(final Context context, long playlistId) {
        if (playlistId == -1) {
            return LAST_ADD.getSongCount();
        } else if(playlistId == -2){
            return LAST_PLAY.getSongCount();
        }else if(playlistId == -3){
            return TOP_TRACK.getSongCount();
        } else {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{"album_id"}, null, null, null);
            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
            }
            closeCursor(cursor);
            return count;
        }
    }

    public static long getAlbumIdFromPlayList(final Context context, long playlistId) {
        if (playlistId == -1) {
            return TrackLoader.getAlbumIdFromLastAdd(context);
        } else if(playlistId == -2){
            long trackId = LastPlayStore.getInstance(context).queryFirstId(context);
            Track track = TrackLoader.getTrack(context, trackId);
            if (track != null) {
                return track.getAlbumId();
            }
            return -1;
        } else if(playlistId == -3){
            long trackId = LastPlayStore.getInstance(context).queryLastId(context);
            Track track = TrackLoader.getTrack(context, trackId);
            if (track != null) {
                return track.getAlbumId();
            }
            return -1;
        }
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                new String[]{"album_id"}, null, null, null);
        long albumId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            albumId = cursor.getLong(0);
        }
        closeCursor(cursor);
        return albumId;
    }

}
