package guepardoapps.guepardonotes.common;

public class Constants {
	/********************** Debugging *********************/
	public static final boolean DEBUGGING_ENABLED = false;

	/************************ Color ***********************/
	public static final int ACTION_BAR_COLOR = 0xff303f9f;

	/*********************** Database *********************/
	public static final String DATABASE_NAME = "Notesdb";
	public static final String DATABASE_TABLE = "Notestable";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_KEY_ROWID = "_id";
	public static final String DATABASE_KEY_TITLE = "title_value";
	public static final String DATABASE_KEY_NOTES = "note_value";

	/********************** SharedPref ********************/
	public static final String SHARED_PREF_NAME = "GUEPARDO_NOTES_DATA";

	/********************** BundleData ********************/
	public static final String BUNDLE_NOTE_ID = "BUNDLE_NOTE_ID";
	public static final String BUNDLE_NOTE_TITLE = "BUNDLE_NOTE_TITLE";
	public static final String BUNDLE_NOTE_CONTENT = "BUNDLE_NOTE_CONTENT";
}