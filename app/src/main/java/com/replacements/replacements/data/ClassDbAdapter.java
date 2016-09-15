package com.replacements.replacements.data;

import com.replacements.replacements.models.ClassTask;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ClassDbAdapter {
    private static final String DEBUG_TAG = "ClassDbAdapter";

    public static final String DB_TABLE = "profile_classes";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY";// AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_CLASS = "class_name";
    public static final String CLASS_OPTIONS = "TEXT NOT NULL";
    public static final int CLASS_COLUMN = 1;
    public static final String KEY_SELECTED = "selected";
    public static final String SELECTED_OPTIONS = "INTEGER NOT NULL";
    public static final int SELECTED_COLUMN = 2;

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

    public ClassDbAdapter(Context context) {
        this.context = context;
    }

    public ClassDbAdapter open(){
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

    public long insertClass(boolean selected, ClassTask task) {
        long id = task.getId();
        String class_name;
        if(task.getName() != null) {
            class_name = task.getName();
        }else{
            class_name = "";
        }
        return insertClass(selected, id, class_name);
    }

    public long insertClass(boolean selected, long id, String class_name) {
        ContentValues newClassValues = new ContentValues();
        newClassValues.put(KEY_ID, id);
        newClassValues.put(KEY_CLASS, class_name);
        newClassValues.put(KEY_SELECTED, selected? 1 : 0);
        return db.insert(DB_TABLE, null, newClassValues);
    }

    public boolean updateClass(ClassTask task) {
        long id = task.getId();
        String class_name;
        if(task.getName() != null) {
            class_name = task.getName();
        }else{
            class_name = "";
        }
        return updateClass(id, class_name);
    }

    public boolean updateClass(long id, String class_name) {
        String where = KEY_ID + "=" + id;
        ContentValues updateClassValues = new ContentValues();
        updateClassValues.put(KEY_CLASS, class_name);
        return db.update(DB_TABLE, updateClassValues, where, null) > 0;
    }

    public boolean updateClassSelect(boolean selected, long id) {
        String where = KEY_ID + "=" + id;
        ContentValues updateClassValues = new ContentValues();
        updateClassValues.put(KEY_SELECTED, selected? 1 : 0);
        return db.update(DB_TABLE, updateClassValues, where, null) > 0;
    }

    public boolean deleteClass(long id){
        String where = KEY_ID + "=" + id;
        Log.i("ClassDbAdapter", "1");
        return db.delete(DB_TABLE, where, null) > 0;
    }
    public boolean deleteClass(String class_name){
        String where = KEY_CLASS + "=\"" + class_name + "\"";
        Log.i("ClassDbAdapter", "1");
        return db.delete(DB_TABLE, where, null) > 0;
    }

    public Cursor getAllClasses() {
        String[] columns = {KEY_ID, KEY_CLASS, KEY_SELECTED};
        return db.query(DB_TABLE, columns, null, null, null, null, KEY_CLASS + " ASC");
    }

    public Cursor getAllIds() {
        String[] columns = {KEY_ID};
        return db.query(DB_TABLE, columns, null, null, null, null, null);
    }

    public Cursor getAllIdsSelected() {
        String[] columns = {KEY_ID, KEY_SELECTED};
        return db.query(DB_TABLE, columns, null, null, null, null, KEY_ID + " ASC");
    }

    public ClassTask getClass(long id) {
        String[] columns = {KEY_ID, KEY_CLASS};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_TABLE, columns, where, null, null, null, null);
        ClassTask task = null;
        if(cursor != null && cursor.moveToFirst()) {
            String class_name = cursor.getString(CLASS_COLUMN);
            task = new ClassTask(id, class_name);
            cursor.close();
        }
        return task;
    }
}