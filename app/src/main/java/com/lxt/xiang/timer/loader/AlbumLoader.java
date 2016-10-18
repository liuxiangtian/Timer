package com.lxt.xiang.timer.loader;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.lxt.xiang.timer.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumLoader {

    public static final String SORT_ALBUM = MediaStore.Audio.Albums.ALBUM;
    public static final String SORT_ALBUM_DESC = MediaStore.Audio.Albums.ALBUM + " DESC";
    public static final String SORT_ARTIST = MediaStore.Audio.Albums.ARTIST;
    public static final String SORT_NUMS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

    private static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM_ART
    };

    public static Cursor makeCursor(final Context context, final String sort) {
        return context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION, null, null, sort);
    }

    public static List<Album> loadAlbums(final Context context, final String sort) {
        Cursor cursor = makeCursor(context, sort);
        return getAlbumsFromCursor(cursor);
    }

    @NonNull
    private static List<Album> getAlbumsFromCursor(Cursor cursor) {
        List<Album> alba = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String album = cursor.getString(1);
                String artist = cursor.getString(2);
                long artistId = cursor.getLong(3);
                int trackNum = cursor.getInt(4);
                String albumArt = cursor.getString(5);
                alba.add(new Album(id, album, artist, artistId, trackNum, albumArt));
            } while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
            cursor=null;
        }
        return alba;
    }

    public static List<Album> loadAlbumsByName(final Context context, String query) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                PROJECTION, "album LIKE ?", new String[]{"%"+query+"%"}, null);
        return getAlbumsFromCursor(cursor);
    }

}
