package com.lxt.xiang.timer.loader;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.lxt.xiang.timer.model.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistLoader {

    public static final String SORT_ARTIST = MediaStore.Audio.Artists.ARTIST;
    public static final String SORT_ARTIST_DESC = MediaStore.Audio.Artists.ARTIST + " DESC";
    public static final String SORT_ALBUM_NUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
    public static final String SORT_TRACK_NUMS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;

    private static final String[] PROJECTION = new String[]{
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
    };

    public static Cursor makeCursor(final Context context, final String sort) {
        return context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                PROJECTION,
                null,
                null, sort);
    }

    public static List<Artist> loadArtists(final Context context, final String sort) {
        Cursor cursor = makeCursor(context, sort);
        return getArtistsFromCursor(cursor);
    }

    @NonNull
    private static List<Artist> getArtistsFromCursor(Cursor cursor) {
        List<Artist> alba = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String artist = cursor.getString(1);
                int albumNum = cursor.getInt(2);
                int trackNum = cursor.getInt(3);
                alba.add(new Artist(id, artist, albumNum, trackNum));
            } while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
            cursor=null;
        }
        return alba;
    }

    public static long loadAlbumIdByArtist(final Context context, final long artistId) {
        Cursor cursor =  context.getContentResolver().query(
                MediaStore.Audio.Artists.Albums.getContentUri("external", artistId),
                new String[]{"_id"},
                null, null, null);
        long albumId = -1;
        if (cursor!=null && cursor.moveToFirst()){
            albumId = cursor.getLong(0);
        }
        if(cursor!=null){
            cursor.close();
            cursor=null;
        }
        return albumId;
    }


    public static List<Artist> loadArtistsByName(final Context context, String query) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                PROJECTION,
                "artist LIKE ?",
                new String[]{"%"+query+"%"}, null);
        return getArtistsFromCursor(cursor);
    }
}
