package com.studytor.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.studytor.app.models.ReplacementTask;

public class ReplacementDbAdapter {
    private static final String DEBUG_TAG = "SqLiteReplManager";

    public static final String DB_TODAY_TABLE = "replacements_today";
    public static final String DB_TOMORROW_TABLE = "replacements_tomorrow";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY";// AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_VER = "ver";
    public static final String VER_OPTIONS = "TEXT";
    public static final int VER_COLUMN = 1;

    public static final String KEY_NUMBER = "number";
    public static final String NUMBER_OPTIONS = "INTEGER";
    public static final int NUMBER_COLUMN = 2;

    public static final String KEY_FIRST_NUMBER = "first_number";
    public static final String FIRST_NUMBER_OPTIONS = "INTEGER";
    public static final int FIRST_NUMBER_COLUMN = 3;

    public static final String KEY_LAST_NUMBER = "last_number";
    public static final String LAST_NUMBER_OPTIONS = "INTEGER";
    public static final int LAST_NUMBER_COLUMN = 4;

    public static final String KEY_REPLACEMENT = "replacement";
    public static final String REPLACEMENT_OPTIONS = "TEXT";
    public static final int REPLACEMENT_COLUMN = 5;

    public static final String KEY_DEFAULT_INTEGER = "default_integer";
    public static final String DEFAULT_INTEGER_OPTIONS = "INTEGER";
    public static final int DEFAULT_INTEGER_COLUMN = 6;

    public static final String KEY_CLASS_NUMBER = "class_number";
    public static final String CLASS_NUMBER_OPTIONS = "INTEGER";
    public static final int CLASS_NUMBER_COLUMN = 7;

    private SQLiteDatabase db;
    private Context context;
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

    public ReplacementDbAdapter(Context context) {
        this.context = context;
    }

    public ReplacementDbAdapter open(){
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

    public long insertReplacement(boolean today, ReplacementTask task) {
        long id = task.getId();
        String ver = task.getVer();
        String number;
        String replacement;
        long default_integer;
        long class_number;
        if(task.getNumber() != null) {
            number = task.getNumber();
            //int first_number = task.getFirstNumber();
            //int last_number = task.getLastNumber();
        }else{
            number = "";
        }
        if(task.getReplacement() != null) {
            replacement = task.getReplacement();
        }else{
            replacement = "";
        }
        default_integer = task.getDefaultInteger();
        class_number = task.getClassNumber();
        return insertReplacement(today, id, ver, number, replacement, default_integer, class_number);
    }

    public long insertReplacement(boolean today, long id, String ver, String number, String replacement, long default_integer, long class_number) {
        ContentValues newReplacementValues = new ContentValues();
        newReplacementValues.put(KEY_ID, id);
        newReplacementValues.put(KEY_VER, ver);
        newReplacementValues.put(KEY_NUMBER, number);
        String[] array_number;
        int first_number;
        int last_number;
        if(!number.equals("")) {
            array_number = number.split(",");
        }else{
            array_number = null;
        }
        first_number = (array_number != null && array_number.length > 0 && !number.equals("")) ? Integer.parseInt(array_number[0]) : 0;
        last_number = (array_number != null && array_number.length > 0 && !number.equals(""))? Integer.parseInt(array_number[array_number.length - 1]) : 0;
        newReplacementValues.put(KEY_FIRST_NUMBER, first_number);
        newReplacementValues.put(KEY_LAST_NUMBER, last_number);
        newReplacementValues.put(KEY_REPLACEMENT, replacement);
        newReplacementValues.put(KEY_DEFAULT_INTEGER, default_integer);
        newReplacementValues.put(KEY_CLASS_NUMBER, class_number);
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.insert(tableName, null, newReplacementValues);
    }

    public boolean updateReplacement(boolean today, ReplacementTask task) {
        long id = task.getId();
        String ver = task.getVer();
        String number;
        String replacement;
        long default_integer;
        long class_number;
        if(task.getNumber() != null) {
            number = task.getNumber();
            //int first_number = task.getFirstNumber();
            //int last_number = task.getLastNumber();
        }else{
            number = "";
        }
        if(task.getReplacement() != null) {
            replacement = task.getReplacement();
        }else{
            replacement = "";
        }
        default_integer = task.getDefaultInteger();
        class_number = task.getClassNumber();
        return updateReplacement(today, id, ver, number, replacement, default_integer, class_number);
    }

    public boolean updateReplacement(boolean today, long id, String ver, String number, String replacement, long default_integer, long class_number) {
        String where = KEY_ID + "=" + id;
        ContentValues updateReplacementValues = new ContentValues();
        updateReplacementValues.put(KEY_VER, ver);
        updateReplacementValues.put(KEY_NUMBER, number);
        String[] array_number;
        int first_number;
        int last_number;
        if(!number.equals("")) {
            array_number = number.split(",");
        }else{
            array_number = null;
        }
        first_number = (array_number != null && array_number.length > 0 && !number.equals(""))? Integer.parseInt(array_number[0]) : 0;
        last_number = (array_number != null && array_number.length > 0 && !number.equals(""))? Integer.parseInt(array_number[array_number.length - 1]) : 0;
        updateReplacementValues.put(KEY_FIRST_NUMBER, first_number);
        updateReplacementValues.put(KEY_LAST_NUMBER, last_number);
        updateReplacementValues.put(KEY_REPLACEMENT, replacement);
        updateReplacementValues.put(KEY_DEFAULT_INTEGER, default_integer);
        updateReplacementValues.put(KEY_CLASS_NUMBER, class_number);
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.update(tableName, updateReplacementValues, where, null) > 0;
    }

    public boolean deleteReplacement(boolean today, long id){
        String where = KEY_ID + "=" + id;
        Log.i("ClassDbAdapter", "1");
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.delete(tableName, where, null) > 0;
    }

    public boolean deleteAllReplacements(boolean today){
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.delete(tableName, null, null) > 0;
    }

    public Cursor getAllReplacements(boolean today) {
        String[] columns = {KEY_ID, KEY_VER, KEY_NUMBER, KEY_FIRST_NUMBER, KEY_LAST_NUMBER, KEY_REPLACEMENT, KEY_DEFAULT_INTEGER, KEY_CLASS_NUMBER};
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.query(tableName, columns, null, null, null, null, KEY_FIRST_NUMBER + ", " + KEY_LAST_NUMBER + " ASC");
    }

    public Cursor getAllIds(boolean today) {
        String[] columns = {KEY_ID};
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        return db.query(tableName, columns, null, null, null, null, null);
    }

    public ReplacementTask getReplacement(boolean today, long id) {
        String[] columns = {KEY_ID, KEY_VER, KEY_NUMBER, KEY_FIRST_NUMBER, KEY_LAST_NUMBER, KEY_REPLACEMENT, KEY_DEFAULT_INTEGER, KEY_CLASS_NUMBER};
        String where = KEY_ID + "=" + id;
        String tableName = (today)? DB_TODAY_TABLE : DB_TOMORROW_TABLE;
        Cursor cursor = db.query(tableName, columns, where, null, null, null, null);
        ReplacementTask task = null;
        if(cursor != null && cursor.moveToFirst()) {
            String ver = cursor.getString(VER_COLUMN);
            String number = cursor.getString(NUMBER_COLUMN);
            //String first_number = cursor.getString(FIRST_NUMBER_COLUMN);
            //String last_number = cursor.getString(LAST_NUMBER_COLUMN);
            String replacement = cursor.getString(REPLACEMENT_COLUMN);
            long default_integer = cursor.getInt(DEFAULT_INTEGER_COLUMN);
            long class_number = cursor.getInt(CLASS_NUMBER_COLUMN);
            task = new ReplacementTask(id, ver, number, replacement, default_integer, class_number);
            cursor.close();
        }
        return task;
    }
}