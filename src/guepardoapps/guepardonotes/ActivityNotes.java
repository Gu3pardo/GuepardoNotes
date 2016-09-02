package guepardoapps.guepardonotes;

import java.util.ArrayList;

import guepardoapps.toolset.classes.Note;
import guepardoapps.common.Constants;
import guepardoapps.controller.DatabaseController;
import guepardoapps.toolset.controller.NavigationController;
import guepardoapps.customadapter.NoteListAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ActivityNotes extends Activity {

	private ArrayList<Note> _noteList;

	private ListView _listView;
	private ProgressBar _progressBar;
	private Button _btnAdd, _btnImpressum;

	private Context _context;

	private DatabaseController _databaseController;
	private NavigationController _navigationController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_main);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Constants.ACTION_BAR_COLOR));

		_context = this;
		_databaseController = new DatabaseController(_context);
		_navigationController = new NavigationController(_context);

		_listView = (ListView) findViewById(R.id.listView);
		_progressBar = (ProgressBar) findViewById(R.id.progressBar);

		_btnAdd = (Button) findViewById(R.id.goToAddView);
		_btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_navigationController.NavigateTo(ActivityAdd.class, true);
			}
		});

		_btnImpressum = (Button) findViewById(R.id.btnImpressum);
		_btnImpressum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_navigationController.NavigateTo(ActivityImpressum.class, true);
			}
		});

		_noteList = _databaseController.GetNotes();

		_listView.setAdapter(new NoteListAdapter(_context, _noteList, _navigationController));

		_progressBar.setVisibility(View.GONE);
		_listView.setVisibility(View.VISIBLE);
	}

	protected void onResume() {
		super.onResume();
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