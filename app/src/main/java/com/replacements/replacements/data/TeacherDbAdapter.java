package com.replacements.replacements.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.replacements.replacements.models.ClassTask;
import com.replacements.replacements.models.TeacherTask;

public class TeacherDbAdapter {
    private static final String DEBUG_TAG = "TeacherDbAdapter";

    public static final String DB_TABLE = "profile_teachers";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY";// AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_TEACHER = "teacher_name";
    public static final String TEACHER_OPTIONS = "TEXT NOT NULL";
    public static final int TEACHER_COLUMN = 1;
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

    public TeacherDbAdapter(Context context) {
        this.context = context;
    }

    public TeacherDbAdapter open(){
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

    public long insertTeacher(boolean selected, TeacherTask task) {
        long id = task.getId();
        String teacher_name;
        if(task.getName() != null) {
            teacher_name = task.getName();
        }else{
            teacher_name = "";
        }
        return insertTeacher(selected, id, teacher_name);
    }

    public long insertTeacher(boolean selected, long id, String class_name) {
        ContentValues newTeacherValues = new ContentValues();
        newTeacherValues.put(KEY_ID, id);
        newTeacherValues.put(KEY_TEACHER, class_name);
        newTeacherValues.put(KEY_SELECTED, selected? 1 : 0);
        return db.insert(DB_TABLE, null, newTeacherValues);
    }

    public boolean updateTeacher(TeacherTask task) {
        long id = task.getId();
        String teacher_name;
        if(task.getName() != null) {
            teacher_name = task.getName();
        }else{
            teacher_name = "";
        }
        return updateTeacher(id, teacher_name);
    }

    public boolean updateTeacher(long id, String teacher_name) {
        String where = KEY_ID + "=" + id;
        ContentValues updateTeacherValues = new ContentValues();
        updateTeacherValues.put(KEY_TEACHER, teacher_name);
        return db.update(DB_TABLE, updateTeacherValues, where, null) > 0;
    }

    public boolean updateTeacherSelect(boolean selected, long id) {
        String where = KEY_ID + "=" + id;
        ContentValues updateClassValues = new ContentValues();
        updateClassValues.put(KEY_SELECTED, selected? 1 : 0);
        return db.update(DB_TABLE, updateClassValues, where, null) > 0;
    }

    public boolean deleteTeacher(long id){
        String where = KEY_ID + "=" + id;
        Log.i("TeacherDbAdapter", "1");
        return db.delete(DB_TABLE, where, null) > 0;
    }
    public boolean deleteTeacher(String teacher_name){
        String where = KEY_TEACHER + "=\"" + teacher_name + "\"";
        Log.i("TeacherDbAdapter", "1");
        return db.delete(DB_TABLE, where, null) > 0;
    }

    public Cursor getAllTeachers() {
        String[] columns = {KEY_ID, KEY_TEACHER, KEY_SELECTED};
        return db.query(DB_TABLE, columns, null, null, null, null, KEY_TEACHER + " ASC");
    }

    public Cursor getAllIds() {
        String[] columns = {KEY_ID};
        return db.query(DB_TABLE, columns, null, null, null, null, null);
    }

    public Cursor getAllIdsSelected() {
        String[] columns = {KEY_ID, KEY_SELECTED};
        return db.query(DB_TABLE, columns, null, null, null, null, KEY_ID + " ASC");
    }

    public TeacherTask getTeacher(long id) {
        String[] columns = {KEY_ID, KEY_TEACHER};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_TABLE, columns, where, null, null, null, null);
        TeacherTask task = null;
        if(cursor != null && cursor.moveToFirst()) {
            String teacher_name = cursor.getString(TEACHER_COLUMN);
            task = new TeacherTask(id, teacher_name);
            cursor.close();
        }
        return task;
    }
}