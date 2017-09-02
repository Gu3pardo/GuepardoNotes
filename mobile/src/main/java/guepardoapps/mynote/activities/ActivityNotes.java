package guepardoapps.mynote.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rey.material.widget.FloatingActionButton;

import guepardoapps.mynote.R;
import guepardoapps.mynote.common.constants.*;
import guepardoapps.mynote.controller.DatabaseController;
import guepardoapps.mynote.controller.NavigationController;
import guepardoapps.mynote.controller.ReceiverController;
import guepardoapps.mynote.customadapter.NoteListAdapter;
import guepardoapps.mynote.tools.Logger;

public class ActivityNotes extends Activity {
    private static final String TAG = ActivityNotes.class.getSimpleName();
    private Logger _logger;

    private boolean _created;

    private ListView _listView;
    private ProgressBar _progressBar;

    private Context _context;

    private DatabaseController _databaseController;
    private NavigationController _navigationController;
    private ReceiverController _receiverController;

    private BroadcastReceiver _noteDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _listView.setAdapter(new NoteListAdapter(_context, _databaseController.GetNotes()));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_main);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;

        _databaseController = DatabaseController.getInstance();
        _navigationController = new NavigationController(_context);
        _receiverController = new ReceiverController(_context);
        _databaseController.Initialize(_context);

        _listView = findViewById(R.id.listView);
        _progressBar = findViewById(R.id.progressBar);

        FloatingActionButton btnAdd = findViewById(R.id.goToAddView);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("btnAdd onClick");
                _navigationController.NavigateTo(ActivityAdd.class, false);
            }
        });

        FloatingActionButton btnImpressum = findViewById(R.id.btnImpressum);
        btnImpressum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("btnImpressum onClick");
                _navigationController.NavigateTo(ActivityImpressum.class, false);
            }
        });

        _created = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _receiverController.Dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");

        _databaseController.Initialize(_context);
        _receiverController.RegisterReceiver(_noteDeletedReceiver, new String[]{Broadcasts.NOTE_DELETED});

        if (_created) {
            _listView.setAdapter(new NoteListAdapter(_context, _databaseController.GetNotes()));

            _progressBar.setVisibility(View.GONE);
            _listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
        _databaseController.Dispose();
        _receiverController.Dispose();
    }
}