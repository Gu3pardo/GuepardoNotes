package guepardoapps.mynote.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import guepardoapps.mynote.R;
import guepardoapps.mynote.common.constants.*;
import guepardoapps.mynote.controller.AndroidSystemController;
import guepardoapps.mynote.controller.DatabaseController;
import guepardoapps.mynote.controller.NavigationController;
import guepardoapps.mynote.controller.SharedPrefController;
import guepardoapps.mynote.model.Note;
import guepardoapps.mynote.tools.Logger;

public class ActivityBoot extends Activity {
    private static final String TAG = ActivityBoot.class.getSimpleName();
    private Logger _logger;

    private Context _context;

    private AndroidSystemController _androidSystemController;
    private DatabaseController _databaseController;
    private NavigationController _navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_boot);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;

        _androidSystemController = new AndroidSystemController(this);
        _databaseController = DatabaseController.getInstance();
        _databaseController.Initialize(_context);
        _navigationController = new NavigationController(_context);

        SharedPrefController sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);
        if (!sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME)) {
            _databaseController.SaveNote(new Note(0, "Title", getResources().getString(R.string.example)));
            sharedPrefController.SaveBooleanValue(SharedPrefConstants.BUBBLE_STATE, true);
            sharedPrefController.SaveIntegerValue(SharedPrefConstants.BUBBLE_POS_Y, SharedPrefConstants.BUBBLE_DEFAULT_POS_Y);
            sharedPrefController.SaveBooleanValue(SharedPrefConstants.SHARED_PREF_NAME, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");
        _databaseController.Initialize(_context);

        if (_androidSystemController.CurrentAndroidApi() >= android.os.Build.VERSION_CODES.M) {
            _logger.Debug("asking for permission");
            if (_androidSystemController.CheckAPI23SystemPermission(PermissionCodes.SYSTEM_PERMISSION)) {
                navigateToMain();
            }
        } else {
            navigateToMain();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _databaseController.Dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
    }

    private void navigateToMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _navigationController.NavigateTo(ActivityNotes.class, true);
            }
        }, 1500);
    }
}