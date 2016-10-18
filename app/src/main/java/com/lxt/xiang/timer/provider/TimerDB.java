package com.lxt.xiang.timer.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimerDB extends SQLiteOpenHelper {

    private static final String DATABASE = "TIMER_DB";
    private static final int VERSION = 1;
    private static TimerDB sInstance;
    private Context context;
    public static final synchronized TimerDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TimerDB(context.getApplicationContext(), DATABASE, null, VERSION);
        }
        return sInstance;
    }

    public TimerDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        PlayQueueStore.getInstance(context).onCreate(db);
        LastPlayStore.getInstance(context).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PlayQueueStore.getInstance(context).onUpgrade(db, oldVersion, newVersion);
        LastPlayStore.getInstance(context).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PlayQueueStore.getInstance(context).onDowngrade(db, oldVersion, newVersion);
        LastPlayStore.getInstance(context).onDowngrade(db, oldVersion, newVersion);
    }

}