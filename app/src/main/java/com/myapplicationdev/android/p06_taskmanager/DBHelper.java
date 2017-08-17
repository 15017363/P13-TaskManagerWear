package com.myapplicationdev.android.p06_taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tasks.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_TASK = "task";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_TASK_NAME = "task_name";
	private static final String COLUMN_TASK_DESCRIPTION = "task_description";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// CREATE TABLE tasks
		// (_id INTEGER PRIMARY KEY AUTOINCREMENT, task_name TEXT, task_description TEXT);
		String createNoteTableSql = "CREATE TABLE " + TABLE_TASK + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_TASK_NAME + " TEXT," 
				+ COLUMN_TASK_DESCRIPTION + " TEXT )";
		db.execSQL(createNoteTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
		// Create table(s) again
		onCreate(db);
	}

	public long insertTask(String name, String description) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TASK_NAME, name);
		values.put(COLUMN_TASK_DESCRIPTION, description);
		long id = db.insert(TABLE_TASK, null, values);
		db.close();
		return id;
	}
	
	public ArrayList<Task> getAllTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_TASK_NAME
				+ "," + COLUMN_TASK_DESCRIPTION + " FROM "
				+ TABLE_TASK;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String description = cursor.getString(2);
				
				Task task = new Task(id, name, description);
				tasks.add(task);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		return tasks;
	}

	public int updateTask(Task data) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TASK_NAME, data.getName());
		values.put(COLUMN_TASK_DESCRIPTION, data.getDescription());
		String condition = COLUMN_ID + "= ?";
		String[] args = {String.valueOf(data.getId())};
		int result = db.update(TABLE_TASK, values, condition, args);
		if (result < 1){
			Log.d("DBHelper", "Update failed");
		}

		db.close();
		return result;
	}

	public int deleteTask(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		String condition = COLUMN_ID + "= ?";
		String[] args = {String.valueOf(id)};
		int result = db.delete(TABLE_TASK, condition, args);
		if (result < 1){
			Log.d("DBHelper", "Update failed");
		}

		db.close();
		return result;
	}
}
