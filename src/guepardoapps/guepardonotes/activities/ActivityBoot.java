package guepardoapps.guepardonotes.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import guepardoapps.guepardonotes.R;
import guepardoapps.guepardonotes.common.constants.*;
import guepardoapps.guepardonotes.controller.DatabaseController;
import guepardoapps.guepardonotes.model.Note;

import guepardoapps.toolset.common.Logger;
import guepardoapps.toolset.controller.NavigationController;
import guepardoapps.toolset.controller.SharedPrefController;

public class ActivityBoot extends Activity {

	private static final String TAG = ActivityBoot.class.getSimpleName();
	private Logger _logger;

	private Context _context;

	private DatabaseController _databaseController;
	private NavigationController _navigationController;
	private SharedPrefController _sharedPrefController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_boot);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR_COLOR));

		_logger = new Logger(TAG, Enables.DEBUGGING_ENABLED);
		_logger.Debug("onCreate");

		_context = this;
		_databaseController = new DatabaseController(_context);
		_navigationController = new NavigationController(_context);
		_sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

		if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME)) {
			_databaseController
					.SaveNote(new Note(0, "Title", String.format(getResources().getString(R.string.example)), 0, 0, 0));
			_sharedPrefController.SaveBooleanValue(SharedPrefConstants.SHARED_PREF_NAME, true);
		}

		_navigationController.NavigateTo(ActivityNotes.class, true);
	}

	protected void onResume() {
		super.onResume();
		_logger.Debug("onResume");
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