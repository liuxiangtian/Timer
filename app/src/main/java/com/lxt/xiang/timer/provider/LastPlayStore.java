package com.lxt.xiang.timer.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.lxt.xiang.timer.loader.TrackLoader;

public class LastPlayStore {

    private static String TABLE_NAME = "LastPlay_DB";
    private static String ID = "id";
    private static String DATE = "date";
    private static String PLAYCOUNT = "count";

    private static String CREAT_TABLE;
    private static String DROP_TABLE;

    private static LastPlayStore sInstance = null;

    private LastPlayStore() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" LONG NOT NULL, ");
        builder.append(PLAYCOUNT);
        builder.append(" INT, ");
        builder.append(DATE);
        builder.append(" LONG NOT NULL");
        builder.append(");");
        CREAT_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized LastPlayStore getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new LastPlayStore();
        }
        return sInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREAT_TABLE);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREAT_TABLE);
    }

    public Cursor queryIdsByDate(Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        return database.query(TABLE_NAME,
                new String[]{ID}, ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
    }

    public long[] queryIdsByDate(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{ID}, null, null,
                null, null, DATE+" DESC", null);
        return getLongs(cursor);
    }

    public long[] queryIdsByPlayCount(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{ID}, null, null,
                null, null, DATE+" DESC", null);
        return getLongs(cursor);
    }

    private long[] getLongs(Cursor cursor) {
        long[] ids = null;
        if (cursor != null && cursor.moveToFirst()) {
            ids = new long[cursor.getCount()];
                do {
                    ids[cursor.getPosition()] = cursor.getLong(0);
                } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return ids;
    }

    public void deleteAll(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, null, null);
    }

    public void deleteItem(Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateItem(Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(DATE, System.currentTimeMillis());
        database.update(TABLE_NAME, values, null, null);
    }

    public void insertItem(Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(DATE, System.currentTimeMillis());
        database.insert(TABLE_NAME, null, values);
    }

    public void insertOrUpdateItem(Context context, final long id) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{ID, PLAYCOUNT}, ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ID, id);
            values.put(DATE, System.currentTimeMillis());
            if (cursor != null && cursor.moveToFirst()) {
                int num = cursor.getInt(1);
                values.put(PLAYCOUNT, num+1);
                database.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
            } else {
                values.put(PLAYCOUNT, 1);
                database.insert(TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeCursor(cursor);
        }
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public int queryNums(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor =  database.query(TABLE_NAME,
                new String[]{ID}, null, null, null, null, null, null);
        int nums = 0;
        if(cursor!=null && cursor.moveToFirst()){
            nums = cursor.getCount();
            closeCursor(cursor);
        }
        return nums;
    }

    public long queryFirstId(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor =  database.query(TABLE_NAME,
                new String[]{ID}, null, null, null, null, DATE+" DESC", null);
        long id = -1;
        if(cursor!=null && cursor.moveToFirst()){
            id = cursor.getLong(0);
            closeCursor(cursor);
        }
        return id;
    }

    public long queryLastId(Context context) {
        final SQLiteDatabase database = TimerDB.getInstance(context).getReadableDatabase();
        Cursor cursor =  database.query(TABLE_NAME,
                new String[]{ID}, null, null, null, null, PLAYCOUNT+" DESC", null);
        long id = -1;
        if(cursor!=null && cursor.moveToFirst()){
//            if(cursor.getCount()>1){
//                cursor.moveToNext();
//            }
            id = cursor.getLong(0);
            closeCursor(cursor);
        }
        return id;
    }

    public Cursor makeLastPlayTrackCursor(Context context) {
        long[] ids = queryIdsByDate(context);
        return makeCursorFromIds(context, ids);
    }

    public Cursor makeTopTrackCursor(Context context) {
        long[] ids = queryIdsByPlayCount(context);
        return makeCursorFromIds(context, ids);
    }

    @Nullable
    private Cursor makeCursorFromIds(Context context, long[] ids) {
        if(ids==null && ids.length==0) return null;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.length-1; i++) {
            builder.append("_id = ");
            builder.append(ids[i]);
            builder.append(" OR ");
        }
        builder.append("_id = ");
        builder.append(ids[ids.length-1]);
        return TrackLoader.makeCursor(context, builder.toString(), null, null);
    }
}
