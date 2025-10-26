package com.example.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 3; // Incremented version to trigger onUpgrade
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DATE = "date";
    private static final String DESCRIPTION = "description"; // New column
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, " + STATUS + " INTEGER, " + DATE + " TEXT, " + DESCRIPTION + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(DATE, task.getDate());
        cv.put(DESCRIPTION, task.getDescription()); // Save description
        db.insert(TODO_TABLE, null, cv);
    }


    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, DATE + " ASC", null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndexOrThrow(ID)));
                        task.setTask(cur.getString(cur.getColumnIndexOrThrow(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndexOrThrow(STATUS)));
                        task.setDate(cur.getString(cur.getColumnIndexOrThrow(DATE)));
                        task.setDescription(cur.getString(cur.getColumnIndexOrThrow(DESCRIPTION))); // Get description
                        taskList.add(task);
                    } while (cur.moveToNext());
                }

            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }


    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});

    }

    public void updateTask(int id, String task, String date, String description) { // Updated method signature
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(DATE, date);
        cv.put(DESCRIPTION, description);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
    }
}