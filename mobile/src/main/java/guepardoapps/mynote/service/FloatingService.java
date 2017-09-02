package guepardoapps.mynote.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rey.material.widget.FloatingActionButton;

import guepardoapps.mynote.R;
import guepardoapps.mynote.activities.ActivityAdd;
import guepardoapps.mynote.activities.ActivityImpressum;
import guepardoapps.mynote.activities.ActivitySettings;
import guepardoapps.mynote.common.constants.SharedPrefConstants;
import guepardoapps.mynote.controller.DatabaseController;
import guepardoapps.mynote.controller.NavigationController;
import guepardoapps.mynote.controller.SharedPrefController;
import guepardoapps.mynote.customadapter.NoteListAdapter;

public class FloatingService extends Service {
    private Context _context;
    private NavigationController _navigationController;
    private SharedPrefController _sharedPrefController;

    private WindowManager _bubbleViewManager;
    private ImageView _bubble;
    private LayoutParams _bubbleParamsStore;
    private int _bubblePosY = 100;
    private Boolean _movedBubble = false;

    private WindowManager _listViewManager;
    private View _listView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _context = this;
        _navigationController = new NavigationController(_context);
        _sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

        _bubblePosY = _sharedPrefController.LoadIntegerValueFromSharedPreferences(SharedPrefConstants.BUBBLE_POS_Y);

        _bubbleViewManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        _bubble = new ImageView(_context);
        _bubble.setImageResource(R.mipmap.ic_launcher);

        final LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = _bubblePosY;

        _bubbleParamsStore = params;

        _bubble.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (params.x < 0) {
                            params.x = 0;

                            _bubbleParamsStore = params;
                            _bubbleViewManager.updateViewLayout(_bubble, _bubbleParamsStore);
                        }
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (initialX - params.x > 25 || initialY - params.y > 25 || params.x - initialX > 25
                                || params.y - initialY > 25) {
                            _movedBubble = true;
                        }

                        _bubbleParamsStore = params;
                        _bubbleViewManager.updateViewLayout(_bubble, _bubbleParamsStore);
                        return true;
                }
                return false;
            }
        });

        _bubble.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_movedBubble) {
                    _movedBubble = false;
                    params.x = 0;
                    _bubblePosY = params.y;

                    _sharedPrefController.SaveIntegerValue(SharedPrefConstants.BUBBLE_POS_Y, _bubblePosY);

                    _bubbleParamsStore = params;
                    _bubbleViewManager.updateViewLayout(_bubble, _bubbleParamsStore);
                } else {
                    showListView();
                }
            }
        });
        _bubbleViewManager.addView(_bubble, _bubbleParamsStore);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_bubble != null) {
            _bubbleViewManager.removeView(_bubble);
        }
    }

    private void showListView() {
        _listViewManager = (WindowManager) _context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = _context.getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        _listView = View.inflate(_context.getApplicationContext(), R.layout.side_main, null);

        FloatingActionButton btnSettings = _listView.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _navigationController.NavigateTo(ActivitySettings.class, false);
                _listViewManager.removeView(_listView);
            }
        });

        FloatingActionButton btnImpressum = _listView.findViewById(R.id.btnImpressum);
        btnImpressum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _navigationController.NavigateTo(ActivityImpressum.class, false);
                _listViewManager.removeView(_listView);
            }
        });

        FloatingActionButton btnAdd = _listView.findViewById(R.id.goToAddView);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _navigationController.NavigateTo(ActivityAdd.class, false);
                _listViewManager.removeView(_listView);
            }
        });

        FloatingActionButton btnClose = _listView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _listViewManager.removeView(_listView);
            }
        });

        ListView noteListView = _listView.findViewById(R.id.listView);
        ProgressBar progressBar = _listView.findViewById(R.id.progressBar);

        noteListView.setAdapter(new NoteListAdapter(_context, DatabaseController.getInstance().GetNotes()));

        progressBar.setVisibility(View.GONE);
        noteListView.setVisibility(View.VISIBLE);

        _listViewManager.addView(_listView, layoutParams);
    }
}