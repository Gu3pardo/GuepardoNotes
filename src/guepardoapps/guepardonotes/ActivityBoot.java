package guepardoapps.guepardonotes;

import guepardoapps.toolset.classes.Note;
import guepardoapps.common.Constants;
import guepardoapps.controller.DatabaseController;
import guepardoapps.toolset.controller.NavigationController;
import guepardoapps.toolset.controller.SharedPrefController;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class ActivityBoot extends Activity {

	private Context _context;

	private DatabaseController _databaseController;
	private NavigationController _navigationController;
	private SharedPrefController _sharedPrefController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_boot);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Constants.ACTION_BAR_COLOR));

		_context = this;
		_databaseController = new DatabaseController(_context);
		_navigationController = new NavigationController(_context);
		_sharedPrefController = new SharedPrefController(_context, Constants.SHARED_PREF_NAME);

		if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(Constants.SHARED_PREF_NAME)) {
			_databaseController.SaveNote(new Note(0, "Title", String.format(getResources().getString(R.string.example)), 0, 0, 0));
			_sharedPrefController.SaveBooleanValue(Constants.SHARED_PREF_NAME, true);
		}

		_navigationController.NavigateTo(ActivityNotes.class, true);
	}

	protected void onResume() {
		super.onResume();
		_navigationController.NavigateTo(ActivityNotes.class, true);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}