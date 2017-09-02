package guepardoapps.mynote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import guepardoapps.mynote.R;
import guepardoapps.mynote.common.constants.PermissionCodes;
import guepardoapps.mynote.common.constants.SharedPrefConstants;
import guepardoapps.mynote.controller.AndroidSystemController;
import guepardoapps.mynote.controller.SharedPrefController;
import guepardoapps.mynote.service.FloatingService;

public class ActivitySettings extends Activity {
    private Context _context;

    private AndroidSystemController _androidSystemController;
    private SharedPrefController _sharedPrefController;

    private Class<FloatingService> _floatingService = FloatingService.class;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_settings);

        _context = this;
        _androidSystemController = new AndroidSystemController(_context);
        _sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

        Switch bubbleStateSwitch = findViewById(R.id.switch_bubble_state);
        bubbleStateSwitch.setChecked(_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.BUBBLE_STATE));
        bubbleStateSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _sharedPrefController.SaveBooleanValue(SharedPrefConstants.BUBBLE_STATE, isChecked);
                if (isChecked) {
                    if (_androidSystemController.CurrentAndroidApi() >= android.os.Build.VERSION_CODES.M) {
                        _androidSystemController.CheckAPI23SystemPermission(PermissionCodes.SYSTEM_PERMISSION);
                    } else {
                        _context.startService(new Intent(_context, _floatingService));
                    }
                } else {
                    if (_androidSystemController.IsServiceRunning(_floatingService)) {
                        _context.stopService(new Intent(_context, _floatingService));
                    }
                }
            }
        });
    }
}