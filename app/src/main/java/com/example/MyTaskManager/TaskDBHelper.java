package com.example.MyTaskManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "task_manager_database.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "task_id";
    public static final String COLUMN_TITLE = "task_title";
    public static final String COLUMN_DESC = "task_description";
    public static final String COLUMN_DUE_DATE = "task_due_date";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_DESC + " TEXT,"
            + COLUMN_DUE_DATE + " TEXT"
            + ")";

    public TaskDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
}

