package de.jdsoft.gesetze.data;

import java.util.ArrayList;
import java.util.List;

import de.jdsoft.gesetze.data.helper.Law;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gesetze";
	private static final String TABLE_LAWS = "gesetze";

	private static final String KEY_ID = "id";
	private static final String KEY_SHORT_NAME = "short_name";
	private static final String KEY_LONG_NAME = "long_name";
	private static final String KEY_TEXT = "text";


	DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public void onCreate(SQLiteDatabase db) {
		String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_LAWS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_SHORT_NAME + " TEXT,"
				+ KEY_LONG_NAME + " TEXT,"
				+ KEY_TEXT + " TEXT" + ")";
		db.execSQL(CREATE_LAW_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAWS);
		onCreate(db);
	}


	public void addLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_SHORT_NAME, law.getShortName());
		values.put(KEY_LONG_NAME, law.getLongName());
		values.put(KEY_TEXT, law.getText());

		db.insert(TABLE_LAWS, null, values);
		db.close();
	}

	public void addLaws(List<Law> laws) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		for( Law law : laws) {
			values.put(KEY_SHORT_NAME, law.getShortName());
			values.put(KEY_LONG_NAME, law.getLongName());
			values.put(KEY_TEXT, law.getText());

			db.insert(TABLE_LAWS, null, values);
		}

		db.close();
	}

	public Law getLaw(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_LAWS, new String[] { KEY_ID,
				KEY_SHORT_NAME, KEY_LONG_NAME, KEY_TEXT }, KEY_ID + "=?",
						new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Law law = new Law(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));

		return law;
	}

	public List<Law> getAllLaws() {
		List<Law> lawList = new ArrayList<Law>();
		String selectQuery = "SELECT  * FROM " + TABLE_LAWS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Law law = new Law();
				law.setID(Integer.parseInt(cursor.getString(0)));
				law.setShortName(cursor.getString(1));
				law.setLongName(cursor.getString(2));
				law.setText(cursor.getString(2));

				lawList.add(law);
			} while (cursor.moveToNext());
		}

		return lawList;
	}

	public int getLawsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LAWS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	public int updateLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SHORT_NAME, law.getShortName());
		values.put(KEY_LONG_NAME, law.getLongName());
		values.put(KEY_TEXT, law.getText());

		return db.update(TABLE_LAWS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(law.getID()) });
	}

	public void deleteLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LAWS, KEY_ID + " = ?",
				new String[] { String.valueOf(law.getID()) });
		db.close();
	}
}
