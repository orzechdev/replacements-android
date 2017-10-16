package com.studytor.app.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
    private static final String DEBUG_TAG = "DbAdapter";

    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "database.db";

    private static final String CREATE_PROFILE_CLASS_TABLE =
            "CREATE TABLE " + ClassDbAdapter.DB_TABLE + "( " +
                    ClassDbAdapter.KEY_ID + " " + ClassDbAdapter.ID_OPTIONS + ", " +
                    ClassDbAdapter.KEY_CLASS + " " + ClassDbAdapter.CLASS_OPTIONS + ", " +
                    ClassDbAdapter.KEY_SELECTED + " " + ClassDbAdapter.SELECTED_OPTIONS +
                    ");";
    private static final String DROP_PROFILE_CLASS_TABLE =
            "DROP TABLE IF EXISTS " + ClassDbAdapter.DB_TABLE;

    private static final String CREATE_PROFILE_TEACHER_TABLE =
            "CREATE TABLE " + TeacherDbAdapter.DB_TABLE + "( " +
                    TeacherDbAdapter.KEY_ID + " " + TeacherDbAdapter.ID_OPTIONS + ", " +
                    TeacherDbAdapter.KEY_TEACHER + " " + TeacherDbAdapter.TEACHER_OPTIONS + ", " +
                    TeacherDbAdapter.KEY_SELECTED + " " + TeacherDbAdapter.SELECTED_OPTIONS +
                    ");";
    private static final String DROP_PROFILE_TEACHER_TABLE =
            "DROP TABLE IF EXISTS " + TeacherDbAdapter.DB_TABLE;

    private static final String CREATE_REPLACEMENTS_TODAY_TABLE =
            "CREATE TABLE " + ReplacementDbAdapter.DB_TODAY_TABLE + "( " +
                    ReplacementDbAdapter.KEY_ID + " " + ReplacementDbAdapter.ID_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_VER + " " + ReplacementDbAdapter.VER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_NUMBER + " " + ReplacementDbAdapter.NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_FIRST_NUMBER + " " + ReplacementDbAdapter.FIRST_NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_LAST_NUMBER + " " + ReplacementDbAdapter.LAST_NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_REPLACEMENT + " " + ReplacementDbAdapter.REPLACEMENT_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_DEFAULT_INTEGER + " " + ReplacementDbAdapter.DEFAULT_INTEGER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_CLASS_NUMBER + " " + ReplacementDbAdapter.CLASS_NUMBER_OPTIONS +
                    ");";
    private static final String DROP_REPLACEMENTS_TODAY_TABLE =
            "DROP TABLE IF EXISTS " + ReplacementDbAdapter.DB_TODAY_TABLE;

    private static final String CREATE_REPLACEMENTS_TOMORROW_TABLE =
            "CREATE TABLE " + ReplacementDbAdapter.DB_TOMORROW_TABLE + "( " +
                    ReplacementDbAdapter.KEY_ID + " " + ReplacementDbAdapter.ID_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_VER + " " + ReplacementDbAdapter.VER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_NUMBER + " " + ReplacementDbAdapter.NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_FIRST_NUMBER + " " + ReplacementDbAdapter.FIRST_NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_LAST_NUMBER + " " + ReplacementDbAdapter.LAST_NUMBER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_REPLACEMENT + " " + ReplacementDbAdapter.REPLACEMENT_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_DEFAULT_INTEGER + " " + ReplacementDbAdapter.DEFAULT_INTEGER_OPTIONS + ", " +
                    ReplacementDbAdapter.KEY_CLASS_NUMBER + " " + ReplacementDbAdapter.CLASS_NUMBER_OPTIONS +
                    ");";
    private static final String DROP_REPLACEMENTS_TOMORROW_TABLE =
            "DROP TABLE IF EXISTS " + ReplacementDbAdapter.DB_TOMORROW_TABLE;




    // Dodane w DB_VERSION = 2
    private static final String CREATE_SCHEDULE_URL_FILES_TABLE =
            "CREATE TABLE " + ScheduleUrlFilesDbAdapter.DB_TABLE + "( " +
                    ScheduleUrlFilesDbAdapter.KEY_ID + " " + ScheduleUrlFilesDbAdapter.ID_OPTIONS + ", " +
                    ScheduleUrlFilesDbAdapter.KEY_URL + " " + ScheduleUrlFilesDbAdapter.URL_OPTIONS + ", " +
                    ScheduleUrlFilesDbAdapter.KEY_DOWNLOADED + " " + ScheduleUrlFilesDbAdapter.DOWNLOADED_OPTIONS +
                    ");";
    private static final String DROP_SCHEDULE_URL_FILES_TABLE =
            "DROP TABLE IF EXISTS " + ScheduleUrlFilesDbAdapter.DB_TABLE;





    private final Context context;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(this.context, DB_NAME, DB_VERSION);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String dbName, int dbVersion) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(DEBUG_TAG, "Database creating...");

            db.execSQL(CREATE_PROFILE_CLASS_TABLE);
            db.execSQL(CREATE_PROFILE_TEACHER_TABLE);
            db.execSQL(CREATE_REPLACEMENTS_TODAY_TABLE);
            db.execSQL(CREATE_REPLACEMENTS_TOMORROW_TABLE);

            // Dodane w DB_VERSION = 2
            db.execSQL(CREATE_SCHEDULE_URL_FILES_TABLE);

            Log.d(DEBUG_TAG, "All tables" + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(DEBUG_TAG, "Database updating...");
            if(oldVersion == 1 && newVersion == 2){

                Log.d(DEBUG_TAG, "Database updating 1");
                db.execSQL(CREATE_SCHEDULE_URL_FILES_TABLE);
                Log.d(DEBUG_TAG, "Database updated 1");

            }
            Log.d(DEBUG_TAG, "All tables updated from ver." + oldVersion + " to ver." + newVersion);
//            db.execSQL(DROP_PROFILE_CLASS_TABLE);
//            db.execSQL(DROP_PROFILE_TEACHER_TABLE);
//            db.execSQL(DROP_REPLACEMENTS_TODAY_TABLE);
//            db.execSQL(DROP_REPLACEMENTS_TOMORROW_TABLE);
//
//            Log.d(DEBUG_TAG, "Database updating...");
//            Log.d(DEBUG_TAG, "All tables updated from ver." + oldVersion + " to ver." + newVersion);
//            Log.d(DEBUG_TAG, "All data is lost.");
//
//            onCreate(db);
        }
    }

    public DbAdapter open(){
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
}