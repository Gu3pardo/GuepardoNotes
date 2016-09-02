package guepardoapps.common;

/**********************************************************
 * Class holding variables used all over the application *
 **********************************************************/
public class Constants {

	/*********************** Debugging **********************/

	public static boolean DEBUGGING_ENABLED = false;

	/************************* Values ***********************/

	public static int ACTION_BAR_COLOR = 0xff303f9f;

	public static String DATABASE_NAME = "Notesdb";
	public static String DATABASE_TABLE = "Notestable";
	public static int DATABASE_VERSION = 1;

	public static String DATABASE_KEY_ROWID = "_id";
	public static String DATABASE_KEY_TITLE = "title_value";
	public static String DATABASE_KEY_NOTES = "note_value";

	/********************** SharedPref *********************/

	public static String SHARED_PREF_NAME = "GUEPARDO_NOTES_DATA";

	/********************** BundleData *********************/

	public static String BUNDLE_NOTE_ID = "BUNDLE_NOTE_ID";
	public static String BUNDLE_NOTE_TITLE = "BUNDLE_NOTE_TITLE";
	public static String BUNDLE_NOTE_CONTENT = "BUNDLE_NOTE_CONTENT";
}