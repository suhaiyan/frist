package com.vince.helping.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vince.helping.util.DateUtils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseAdapter {
	private static final String DB_NAME = "lostfound.db";
	private static final String PUSH_TABLE = "push_table";
	public static final String ID = "_id";
	public static final String MESSAGE = "message";
	public static final String CREATE_DATE = "create_date";
	private static final String CREATE_TABLE = "create table push_table(_id integer primary key autoincrement,message text,create_date text)";
	private static final int VERSION = 1;
	
	private DatabaseHelper dbHelper;
	
	public DatabaseAdapter(Context context){
		dbHelper = new DatabaseHelper(context);
	}
	//Ìí¼Ó
	public void save(String message){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MESSAGE, message);
		values.put(CREATE_DATE, DateUtils.toDate(new Date()));
		db.insert(PUSH_TABLE, null, values);
		db.close();
	}
	//É¾³ý
	public void delete(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(PUSH_TABLE, "_id=?",  new String[]{String.valueOf(id)});
		db.close();
	}
	
	public ArrayList<HashMap<String,String>> list(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query(PUSH_TABLE, new String[]{ID,MESSAGE,CREATE_DATE}, null, null, null, null, CREATE_DATE+" desc");
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = null;
		while(c.moveToNext()){
			map = new HashMap<String,String>();
			map.put(ID, String.valueOf(c.getInt(0)));
			map.put(MESSAGE, c.getString(1));
			map.put(CREATE_DATE, c.getString(2));
			list.add(map);
		}
		c.close();
		db.close();
		return list;
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion > oldVersion) {
				db.execSQL("drop table if exists push_table");
				db.execSQL(CREATE_TABLE);
			}
		}
	}
}
