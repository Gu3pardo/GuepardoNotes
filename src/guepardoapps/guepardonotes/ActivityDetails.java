package guepardoapps.guepardonotes;

import guepardoapps.toolset.classes.Note;
import guepardoapps.toolset.controller.*;
import guepardoapps.common.*;
import guepardoapps.controller.*;
import guepardoapps.guepardonotes.R;
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
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.Toast;

public class ActivityDetails extends Activity {

	private boolean _noteEdited;

	private Note _note;
	private Note _originalNote;

	private EditText _titleView;
	private EditText _contentView;

	private Button _btnEditSave;
	private Button _btnDelete;

	private ImageButton _btnMail;

	private Context _context;

	private DatabaseController _databaseController;
	private DialogController _dialogController;
	private MailController _mailController;
	private NavigationController _navigationController;
	private NetworkController _networkController;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_details);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Constants.ACTION_BAR_COLOR));

		_noteEdited = false;

		_context = this;
		_databaseController = new DatabaseController(_context);
		_dialogController = new DialogController(_context, getResources().getColor(R.color.TextIcon), getResources().getColor(R.color.Primary));
		_mailController = new MailController(_context);
		_navigationController = new NavigationController(_context);
		_networkController = new NetworkController(_context, _dialogController);

		Bundle details = getIntent().getExtras();
		_note = new Note(details.getInt(Constants.BUNDLE_NOTE_ID), details.getString(Constants.BUNDLE_NOTE_TITLE),
				details.getString(Constants.BUNDLE_NOTE_CONTENT), 0, 0, 0);
		_originalNote = _note;

		_titleView = (EditText) findViewById(R.id.detailTitle);
		_titleView.setText(_note.GetTitle());
		_titleView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_noteEdited = true;

				_btnEditSave.setVisibility(View.VISIBLE);
				_btnDelete.setVisibility(View.INVISIBLE);

				_note.SetTitle(_titleView.getText().toString());
			}
		});

		_contentView = (EditText) findViewById(R.id.detailContent);
		_contentView.setScroller(new Scroller(_context));
		_contentView.setVerticalScrollBarEnabled(true);
		_contentView.setMovementMethod(new ScrollingMovementMethod());
		_contentView.setText(_note.GetContent());
		_contentView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_noteEdited = true;

				_btnEditSave.setVisibility(View.VISIBLE);
				_btnDelete.setVisibility(View.INVISIBLE);

				_note.SetContent(_contentView.getText().toString());
			}
		});

		_btnEditSave = (Button) findViewById(R.id.btnEditSave);
		_btnEditSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_noteEdited) {
					_databaseController.UpdateNote(_note);

					resetEditable();
				}
			}
		});

		_btnDelete = (Button) findViewById(R.id.btnDelete);
		_btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!_noteEdited) {
					_dialogController.ShowDialogDouble("Delete Note?", "Do you want to delete the note?", "Yes",
							deleteNoteCallback, "No", _dialogController.closeDialogCallback, true);
				}
			}
		});

		_btnMail = (ImageButton) findViewById(R.id.btnMail);
		_btnMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_networkController.IsNetworkAvailable()) {
					_mailController.SendMailWithContent(_note.GetTitle(), _note.GetContent());
				} else {
					Toast.makeText(_context, "Sorry, no network available!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (_noteEdited) {
				_dialogController.ShowDialogTriple("Warning!",
						"The created note is not saved! Do you want to save the note?", "Yes", updateNoteCallback, "No",
						showOriginalNoteCallback, "Cancel", _dialogController.closeDialogCallback, true);
			} else {
				_navigationController.NavigateTo(ActivityNotes.class, true);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Runnable updateNoteCallback = new Runnable() {
		public void run() {
			_databaseController.UpdateNote(_note);

			resetEditable();
		}
	};

	private Runnable deleteNoteCallback = new Runnable() {
		public void run() {
			_databaseController.DeleteNote(_note);
			_navigationController.NavigateTo(ActivityNotes.class, true);
		}
	};

	private Runnable showOriginalNoteCallback = new Runnable() {
		public void run() {
			_note = _originalNote;

			resetEditable();

			_titleView.setText(_note.GetTitle());
			_contentView.setText(_note.GetContent());
		}
	};

	private void resetEditable() {
		_noteEdited = false;

		_btnEditSave.setVisibility(View.INVISIBLE);
		_btnDelete.setVisibility(View.VISIBLE);

		_titleView.setFocusable(false);
		_contentView.setFocusable(false);
	}
}