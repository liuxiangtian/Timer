package com.lxt.xiang.timer.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.provider.LastPlayStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrackLoader {

    public static final String SORT_TITLE = MediaStore.Audio.Media.TITLE;
    public static final String SORT_DURATION = MediaStore.Audio.Media.DURATION;
    public static final String SORT_DATE_ADDED = MediaStore.Audio.Media.DATE_ADDED+" DESC";
    public static final String SORT_ARTIST = MediaStore.Audio.Media.ARTIST;
    public static final String SORT_ALBUM = MediaStore.Audio.Media.ALBUM;
    public static final String SORT_YEAR = MediaStore.Audio.Media.YEAR;

    private static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.YEAR
    };

    private static final String SELECTION = " is_music = 1 and duration <> 0 ";

    public static Cursor makeCursor(Context context, String selection,
                                    String[] selectionArgs, String sort) {
        selection = selection == null ? SELECTION : SELECTION + "and " + selection;
        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                selection, selectionArgs, sort);
    }

    public static Cursor makeLastAddCursor(Context context, int nums) {
        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                SELECTION, null, SORT_DATE_ADDED + " limit " + nums);
    }

    public static List<Track> loadTracks(Context context, String sort) {
        Cursor cursor = makeCursor(context, null, null, sort);
        return loadTracksFromCursor(cursor);
    }

    public static Track getTrack(Context context, long id) {
        Cursor cursor = makeCursor(context, " _id = " + id, null, null);
        return getFirstTrackFromCursor(cursor);
    }

    @Nullable
    private static Track getFirstTrackFromCursor(Cursor cursor) {
        Track tra = null;
        if (cursor != null && cursor.moveToFirst()) {
            long trackId = cursor.getLong(0);
            long add = cursor.getLong(1);
            String title = cursor.getString(2);
            long duration = cursor.getLong(3);
            String artist = cursor.getString(4);
            long artistId = cursor.getLong(5);
            String track = cursor.getString(6);
            String album = cursor.getString(7);
            long albumId = cursor.getLong(8);
            int year = cursor.getInt(9);
            tra = new Track(trackId, albumId, artistId, add, title, artist, track, album, duration, year);
        }
        closeCursor(cursor);
        return tra;
    }

    public static Track getRandomTrackFromCursor(Context context) {
        Cursor cursor = makeCursor(context, null, null, null);
        Track tra = null;
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();
            int newPos = new Random().nextInt(count);
            cursor.moveToPosition(newPos);
            long trackId = cursor.getLong(0);
            long add = cursor.getLong(1);
            String title = cursor.getString(2);
            long duration = cursor.getLong(3);
            String artist = cursor.getString(4);
            long artistId = cursor.getLong(5);
            String track = cursor.getString(6);
            String album = cursor.getString(7);
            long albumId = cursor.getLong(8);
            int year = cursor.getInt(9);
            tra = new Track(trackId, albumId, artistId, add, title, artist, track, album, duration, year);
        }
        closeCursor(cursor);
        return tra;
    }

    public static List<Track> loadTracksByAlbumId(Context context, long albumId) {
        Cursor cursor = makeCursor(context, " album_id =" + albumId, null, null);
        return loadTracksFromCursor(cursor);
    }

    public static List<Track> loadTracksByArtistId(Context context, long artistId) {
        Cursor cursor = makeCursor(context, " artist_id =" + artistId, null, null);
        return loadTracksFromCursor(cursor);
    }

    public static Cursor makeCursorByPlaylistId(Context context, long playlistId) {
        Cursor cursor = null;
        if (playlistId == -1) {
            cursor = makeLastAddCursor(context, PlaylistLoader.LAST_ADD.getSongCount());
        } else if (playlistId == -2) {
            cursor = LastPlayStore.getInstance(context).makeLastPlayTrackCursor(context);
        } else if (playlistId == -3) {
            cursor = LastPlayStore.getInstance(context).makeTopTrackCursor(context);
        } else {
            cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    PROJECTION, SELECTION, null, null, null);
        }
        return cursor;
    }

    public static List<Track> loadTracksByPlaylistId(Context context, long playlistId) {
        Cursor cursor = makeCursorByPlaylistId(context, playlistId);
        return loadTracksFromCursor(cursor);
    }

    @NonNull
    private static List<Track> loadTracksFromCursor(Cursor cursor) {
        List<Track> tracks = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                long add = cursor.getLong(1);
                String title = cursor.getString(2);
                long duration = cursor.getLong(3);
                String artist = cursor.getString(4);
                long artistId = cursor.getLong(5);
                String track = cursor.getString(6);
                String album = cursor.getString(7);
                long albumId = cursor.getLong(8);
                int year = cursor.getInt(9);
                tracks.add(new Track(id, albumId, artistId, add, title, artist, track, album, duration, year));
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return tracks;
    }

    public static long getAlbumIdFromLastAdd(Context context) {
        long albumId = -1;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.ALBUM_ID}, SELECTION, null,
                SORT_DATE_ADDED + " limit 1");
        if (cursor != null && cursor.moveToFirst()) {
            albumId = cursor.getLong(0);
        }
        closeCursor(cursor);
        return albumId;
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public static int getLastAddCount(Context context) {
        int nums = 0;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID}, null, null, SORT_DATE_ADDED);
        if (cursor != null) {
            nums = cursor.getCount();
        }
        closeCursor(cursor);
        return nums;
    }

    public static List<Track> loadTracksByName(Context context, String query) {
        Cursor cursor = makeCursor(context,"title LIKE ?", new String[]{"%" + query + "%"}, null);
        return loadTracksFromCursor(cursor);
    }
}
