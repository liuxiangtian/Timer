package com.lxt.xiang.timer.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lxt.xiang.timer.model.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayQueueStore {

    private static String TABLE_NAME = "PLAY_QUEUE_TABLE";
    private static String ID = "id";
    private static String ALBUM_ID = "album_id";
    private static String ARTIST_ID = "artist_id";
    private static String TITLE = "title";
    private static String ARTIST = "artist";
    private static String ALBUM = "album";
    private static String TRACK = "track";
    private static String DURATION = "duration";

    private static String CREAT_TABLE;
    private static String DROP_TABLE;

    private static PlayQueueStore sInstance = null;

    private PlayQueueStore() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" LONG NOT NULL, ");
        builder.append(ALBUM_ID);
        builder.append(" LONG NOT NULL, ");
        builder.append(ARTIST_ID);
        builder.append(" LONG NOT NULL, ");
        builder.append(TITLE);
        builder.append(" STRING NOT NULL, ");
        builder.append(ARTIST);
        builder.append(" STRING NOT NULL, ");
        builder.append(ALBUM);
        builder.append(" STRING NOT NULL, ");
        builder.append(TRACK);
        builder.append(" STRING NOT NULL, ");
        builder.append(DURATION);
        builder.append(" LONG NOT NULL");
        builder.append(");");
        CREAT_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized PlayQueueStore getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlayQueueStore();
        }
        return sInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREAT_TABLE);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREAT_TABLE);
    }

    public Cursor queryIds(final Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        return database.query(TABLE_NAME,
                null, ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, "1");
    }

    public List<Track> queryTracks(final Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null,
                null, null, null, null);
        List<Track> tracks = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Track track = new Track();
                track.setId(cursor.getLong(0));
                track.setAlbumId(cursor.getLong(1));
                track.setArtistId(cursor.getLong(2));
                track.setTitle(cursor.getString(3));
                track.setArtist(cursor.getString(4));
                track.setAlbum(cursor.getString(5));
                track.setTrack(cursor.getString(6));
                track.setDuration(cursor.getLong(7));
                tracks.add(track);
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    public void deleteAll(final Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, null, null);
    }

    public void deleteItem(final Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateItem(Context context, final Track track) {
        if (track == null) {
            return;
        }
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, track.getId());
        values.put(ALBUM_ID, track.getAlbumId());
        values.put(ARTIST_ID, track.getArtistId());
        values.put(TITLE, track.getTitle());
        values.put(TRACK, track.getTrack());
        values.put(ALBUM, track.getAlbum());
        values.put(ARTIST, track.getArtist());
        values.put(DURATION, track.getDuration());
        database.update(TABLE_NAME, values, null, null);
    }

    public void insertItem(Context context, final Track track) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, track.getId());
        values.put(ALBUM_ID, track.getAlbumId());
        values.put(ARTIST_ID, track.getArtistId());
        values.put(TITLE, track.getTitle());
        values.put(TRACK, track.getTrack());
        values.put(ALBUM, track.getAlbum());
        values.put(ARTIST, track.getArtist());
        values.put(DURATION, track.getDuration());
        database.insert(TABLE_NAME, null, values);
    }

    public void insertItem(Context context, final List<Track> tracks) {
        if (tracks == null) {
            return;
        }
        deleteAll(context);
        final SQLiteDatabase db = TimerDB.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        try {
            for (Track track : tracks) {
                ContentValues values = new ContentValues();
                values.put(ID, track.getId());
                values.put(ALBUM_ID, track.getAlbumId());
                values.put(ARTIST_ID, track.getArtistId());
                values.put(TITLE, track.getTitle());
                values.put(TRACK, track.getTrack());
                values.put(ALBUM, track.getAlbum());
                values.put(ARTIST, track.getArtist());
                values.put(DURATION, track.getDuration());
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

}
