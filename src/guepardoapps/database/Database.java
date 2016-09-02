package guepardoapps.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import guepardoapps.toolset.classes.Note;
import guepardoapps.common.Constants;

public class Database {

	// Context
	public static final String KEY_ROWID = Constants.DATABASE_KEY_ROWID;
	public static final String KEY_TITLE = Constants.DATABASE_KEY_TITLE;
	public static final String KEY_NOTES = Constants.DATABASE_KEY_NOTES;

	// Database Data
	private static final String DATABASE_NAME = Constants.DATABASE_NAME;
	private static final String DATABASE_TABLE = Constants.DATABASE_TABLE;
	private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;

	// Other Stuff
	private DbHelper helper;
	private final Context context;
	private SQLiteDatabase database;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(" CREATE TABLE " + DATABASE_TABLE + " ( " 
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_TITLE + " TEXT NOT NULL, "
					+ KEY_NOTES + " TEXT NOT NULL); ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(" DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}

		public void remove(Context context) {
			context.deleteDatabase(Constants.DATABASE_NAME);
		}
	}

	public Database(Context _context) {
		context = _context;
	}

	public Database Open() throws SQLException {
		helper = new DbHelper(context);
		database = helper.getWritableDatabase();
		return this;
	}

	public void Close() {
		helper.close();
	}

	public long CreateEntry(Note newNote) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TITLE, newNote.GetTitle());
		cv.put(KEY_NOTES, newNote.GetContent());
		return database.insert(DATABASE_TABLE, null, cv);
	}

	public ArrayList<Note> GetNotes() {
		String[] columns = new String[] { KEY_ROWID, KEY_TITLE, KEY_NOTES };
		Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
		ArrayList<Note> result = new ArrayList<Note>();

		int idIndex = c.getColumnIndex(KEY_ROWID);
		int titleIndex = c.getColumnIndex(KEY_TITLE);
		int noteIndex = c.getColumnIndex(KEY_NOTES);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(new Note(c.getInt(idIndex), c.getString(titleIndex), c.getString(noteIndex), 0, 0, 0));
		}

		return result;
	}

	public void Update(Note updateNote) throws SQLException {
		ContentValues args = new ContentValues();
		args.put(KEY_NOTES, updateNote.GetContent());
		database.update(DATABASE_TABLE, args, KEY_ROWID + "=" + updateNote.GetId(), null);
	}

	public void Delete(Note deleteNote) throws SQLException {
		database.delete(DATABASE_TABLE, KEY_ROWID + "=" + deleteNote.GetId(), null);
	}

	public void Remove() {
		helper.remove(context);
	}
}
