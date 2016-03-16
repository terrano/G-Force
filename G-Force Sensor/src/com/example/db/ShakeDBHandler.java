package com.example.db;

import com.example.g_forcesensor.MultipleSeriesDataset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class ShakeDBHandler extends SQLiteOpenHelper{
	
	private SQLiteDatabase db = null;
	
	public static final String DATABASE_NAME = "my_app.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String SHAKE_TABLE = "shakes";	
	public static final String COLUMN_ID = "_id";
	public static final String SHAKE_COLUMN = "shake";
	public static final String DATE_OF_SHAKE_COLUMN = "dateOfShake";
	public static final String SHAKE_MAX_COLUMN = "shake_max";
	public static final String DURATION_COLUMN = "duration";
	
	private static final String onCreateScript_ShakeTable = "CREATE TABLE " + SHAKE_TABLE + 
			" ( " + COLUMN_ID + " integer primary key autoincrement, " + SHAKE_COLUMN + " string not null, "
			+ DATE_OF_SHAKE_COLUMN + " string not null, "
			+ DURATION_COLUMN + " string not null, "
			+ SHAKE_MAX_COLUMN + " string not null " + " );";
	
	private static final String x_C = "a";
	private static final String y_C = "b";
	private static final String z_C = "c";
	private static final String g_C = "d";
	
	public ShakeDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.db = this.getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(onCreateScript_ShakeTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public void closeDB(){
		if (db != null) {
			if (db.isOpen()) this.db.close();
			this.db = null;
		}
	}
	
	public Cursor getList(){
		Cursor cursor = null;		
		try{
			cursor = this.db.query(SHAKE_TABLE, new String[] {COLUMN_ID, SHAKE_COLUMN, DATE_OF_SHAKE_COLUMN, DURATION_COLUMN, SHAKE_MAX_COLUMN}, null, null, null, null, null);
		} catch (SQLException e){}
		
		return cursor;	
	}
	
	private Cursor getJumpId(long id) {
		if (id < 0) return null;
		 		
		Cursor mCursor = null;
		
		try {
			mCursor = this.db.query(true, SHAKE_TABLE,new String[] { COLUMN_ID, SHAKE_COLUMN, DATE_OF_SHAKE_COLUMN, DURATION_COLUMN, SHAKE_MAX_COLUMN }, COLUMN_ID + "=" + id, null,	null, null, null, null);
			mCursor.moveToFirst();
		} catch (SQLException e) {} 
		finally {
			if (mCursor != null) mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public ShakeObject loadJump(long id){
		Cursor mCursor = getJumpId(id);		
		if (mCursor == null) return null;
		
		String name = mCursor.getString(1);
		ShakeObject jump = new ShakeObject(name, mCursor.getString(2), mCursor.getString(3), mCursor.getString(4));
		mCursor.close();
		mCursor = null;
		try {
			mCursor = this.db.query(name, new String[] { x_C, y_C, z_C, g_C}, null,	null, null, null, null, null);
		} catch (SQLException e) {} 
		finally {
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else{
				jump.onDestroy();
				jump = null;
				return null;
			}
		}
		
		MultipleSeriesDataset msds = new MultipleSeriesDataset();		
		for (int i = 0; i < mCursor.getCount(); i++){
			msds.addPoint(i, mCursor.getDouble(0), mCursor.getDouble(1), mCursor.getDouble(2), mCursor.getDouble(3));			
			mCursor.moveToNext();
		}
		jump.setmDataset(msds);
		
		return jump;
	}
	
	public void dropJump(long id){
		if (id < 0) return;
		Cursor mCursor = getJumpId(id);		
		
		if (mCursor == null) return;
		
		String name = mCursor.getString(1);
		mCursor.close();
		
		String query = "drop table if exists " + name;
		String query_row = "delete from " + SHAKE_TABLE + " where " + SHAKE_COLUMN + " = '" + name + "'";
		
		try {
			this.db.beginTransaction();
			this.db.setTransactionSuccessful();
			this.db.execSQL(query);
			this.db.execSQL(query_row);
			this.db.endTransaction();
		} catch (SQLException e) {}		
	}
	
	public void dropMultipleJumps(long[] dropList){
		if (dropList == null) return;
		
		for (int i = 0; i < dropList.length; i++) 
			dropJump(dropList[i]);
	}
	
	public void insert(ShakeObject jo){
		if (jo == null) return;
		
		String table = null;
		table = jo.getShakeName();		
		if (table == null) return;
		
		String template = "create table " + table + " ("
				+ x_C + " double not null, " + y_C + " double not null, " + z_C + " double not null, " 
				+ g_C + " double not null " + ");";
		
		MultipleSeriesDataset mdl = jo.getmDataset();
		int length = mdl.getDatasetX().getItemCount(); 
		if (length < 1) return;
		double[][] list_new = new double[length][4];
		for (int i = 0; i < length; i++){
			list_new[i][0] =  mdl.getDatasetX().getY(i);
			list_new[i][1] =  mdl.getDatasetY().getY(i);
			list_new[i][2] =  mdl.getDatasetZ().getY(i);
			list_new[i][3] =  mdl.getDatasetG().getY(i);
		}
		
		try{			
			this.db.beginTransaction();
			this.db.setTransactionSuccessful();
			
			ContentValues initialValues = new ContentValues();
			initialValues.put(SHAKE_COLUMN, jo.getShakeName());
			initialValues.put(DATE_OF_SHAKE_COLUMN, jo.getDate());
			initialValues.put(DURATION_COLUMN, jo.getDuration());			
			initialValues.put(SHAKE_MAX_COLUMN, jo.getCurrentMax());			
			this.db.insert(SHAKE_TABLE, null, initialValues);			
			initialValues.clear();
			
			this.db.execSQL(template);
			
			for (int i = 0; i < length; i++){	
				initialValues = new ContentValues();
				initialValues.put(x_C, String.valueOf(list_new[i][0]));
				initialValues.put(y_C, String.valueOf(list_new[i][1]));
				initialValues.put(z_C, String.valueOf(list_new[i][2]));
				initialValues.put(g_C, String.valueOf(list_new[i][3]));
						
				this.db.insert(table, null, initialValues);
						
				initialValues.clear();
				initialValues = null;
			}	
			
			this.db.endTransaction();			
		} catch (SQLException e) {}	
	}
}
