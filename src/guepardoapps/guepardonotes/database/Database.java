package guepardoapps.guepardonotes.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import guepardoapps.guepardonotes.common.constants.DatabaseConstants;
import guepardoapps.guepardonotes.model.Note;

public class Database {

	public static final String KEY_ROWID = DatabaseConstants.DATABASE_KEY_ROWID;
	public static final String KEY_TITLE = DatabaseConstants.DATABASE_KEY_TITLE;
	public static final String KEY_NOTES = DatabaseConstants.DATABASE_KEY_NOTES;

	private static final String DATABASE_NAME = DatabaseConstants.DATABASE_NAME;
	private static final String DATABASE_TABLE = DatabaseConstants.DATABASE_TABLE;
	private static final int DATABASE_VERSION = DatabaseConstants.DATABASE_VERSION;

	private DatabaseHelper _databaseHelper;
	private final Context _context;
	private SQLiteDatabase _database;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(
					" CREATE TABLE " + DATABASE_TABLE + " ( " 
							+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
							+ KEY_TITLE + " TEXT NOT NULL, " 
							+ KEY_NOTES + " TEXT NOT NULL); ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL(" DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(database);
		}

		public void Remove(Context context) {
			context.deleteDatabase(DatabaseConstants.DATABASE_NAME);
		}
	}

	public Database(Context context) {
		_context = context;
	}

	public Database Open() throws SQLException {
		_databaseHelper = new DatabaseHelper(_context);
		_database = _databaseHelper.getWritableDatabase();
		return this;
	}

	public void Close() {
		_databaseHelper.close();
	}

	public long CreateEntry(Note newNote) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(KEY_TITLE, newNote.GetTitle());
		contentValues.put(KEY_NOTES, newNote.GetContent());
		
		return _database.insert(DATABASE_TABLE, null, contentValues);
	}

	public ArrayList<Note> GetNotes() {
		String[] columns = new String[] { KEY_ROWID, KEY_TITLE, KEY_NOTES };
		Cursor cursor = _database.query(DATABASE_TABLE, columns, null, null, null, null, null);
		ArrayList<Note> result = new ArrayList<Note>();

		int idIndex = cursor.getColumnIndex(KEY_ROWID);
		int titleIndex = cursor.getColumnIndex(KEY_TITLE);
		int noteIndex = cursor.getColumnIndex(KEY_NOTES);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			result.add(new Note(cursor.getInt(idIndex), cursor.getString(titleIndex), cursor.getString(noteIndex), 0, 0,
					0));
		}

		return result;
	}

	public void Update(Note updateNote) throws SQLException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NOTES, updateNote.GetContent());
		_database.update(DATABASE_TABLE, contentValues, KEY_ROWID + "=" + updateNote.GetId(), null);
	}

	public void Delete(Note deleteNote) throws SQLException {
		_database.delete(DATABASE_TABLE, KEY_ROWID + "=" + deleteNote.GetId(), null);
	}

	public void Remove() {
		_databaseHelper.Remove(_context);
	}
}
