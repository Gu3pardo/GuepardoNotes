package guepardoapps.guepardonotes;

import guepardoapps.toolset.classes.Note;
import guepardoapps.common.*;
import guepardoapps.controller.DatabaseController;
import guepardoapps.toolset.controller.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

public class ActivityAdd extends Activity {

	private String _title;
	private String _content;

	private EditText _editTitle;
	private EditText _editContent;
	private Button _btnSave;

	private Context _context;

	private DatabaseController _databaseController;
	private DialogController _dialogController;
	private NavigationController _navigationController;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_add);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Constants.ACTION_BAR_COLOR));

		_context = this;

		_databaseController = new DatabaseController(_context);
		_dialogController = new DialogController(_context, getResources().getColor(R.color.TextIcon),
				getResources().getColor(R.color.Primary));
		_navigationController = new NavigationController(_context);

		_editTitle = (EditText) findViewById(R.id.addTitle);
		_editTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_title = _editTitle.getText().toString();
			}
		});

		_editContent = (EditText) findViewById(R.id.addContent);
		_editContent.setScroller(new Scroller(_context));
		_editContent.setMaxLines(1);
		_editContent.setVerticalScrollBarEnabled(true);
		_editContent.setMovementMethod(new ScrollingMovementMethod());
		_editContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_content = _editContent.getText().toString();
			}
		});

		_btnSave = (Button) findViewById(R.id.btnSave);
		_btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trySaveNewNoteCallback.run();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (_title != null || _content != null) {
				_dialogController.ShowDialogTriple("Warning!",
						"The created note is not saved! Do you want to save the note?", "Yes", trySaveNewNoteCallback,
						"No", finishCallback, "Cancel", _dialogController.closeDialogCallback, true);
			} else {
				finishCallback.run();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Runnable trySaveNewNoteCallback = new Runnable() {
		public void run() {
			if (_title == "") {
				Toast.makeText(_context, "Please enter a title!", Toast.LENGTH_SHORT).show();
				return;
			}

			if (_content == "") {
				Toast.makeText(_context, "Please enter a note!", Toast.LENGTH_SHORT).show();
				return;
			}

			_databaseController.SaveNote(new Note(0, _title, _content, 0, 0, 0));
			finishCallback.run();
		}
	};

	private Runnable finishCallback = new Runnable() {
		public void run() {
			_navigationController.NavigateTo(ActivityNotes.class, true);
		}
	};
}