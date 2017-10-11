package com.studytor.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleUrlFilesDbAdapter {
    private static final String DEBUG_TAG = "ScheduleUrlFilesDbAdapter";

    public static final String DB_TABLE = "schedule_url_files";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY";// AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_URL = "url";
    public static final String URL_OPTIONS = "TEXT NOT NULL";
    public static final int URL_COLUMN = 1;
    public static final String KEY_DOWNLOADED = "downloaded";
    public static final String DOWNLOADED_OPTIONS = "INTEGER NOT NULL";
    public static final int DOWNLOADED_COLUMN = 2;

    private final Context context;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DbAdapter.DB_NAME, null, DbAdapter.DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public ScheduleUrlFilesDbAdapter(Context context) {
        this.context = context;
    }

    public ScheduleUrlFilesDbAdapter open(){
        dbHelper = new DatabaseHelper(context);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertUrl(long id, String url) {
        return insertUrl(id, url, false);
    }

    public long insertUrl(long id, String url, boolean downloaded) {
        if(url == null)
            url = "";
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_ID, id);
        newValues.put(KEY_URL, url);
        newValues.put(KEY_DOWNLOADED, downloaded? 1 : 0);
        return db.insert(DB_TABLE, null, newValues);
    }

    public boolean setDownloaded(String url, boolean downloaded) {
        if(url == null)
            url = "";
        String where = KEY_URL + "='" + url + "'";
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_DOWNLOADED, downloaded? 1 : 0);
        return db.update(DB_TABLE, updateValues, where, null) > 0;
    }

    public Cursor getAllUrls(boolean downloaded) {
        String[] columns = {KEY_ID, KEY_URL, KEY_DOWNLOADED};
        String where = KEY_DOWNLOADED + "=" + (downloaded? 1 : 0);
        return db.query(DB_TABLE, columns, where, null, null, null, null);
    }

    public boolean deleteAllUrls(){
        return db.delete(DB_TABLE, null, null) > 0;
    }
}