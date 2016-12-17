package guepardoapps.guepardonotes.customadapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import guepardoapps.guepardonotes.R;
import guepardoapps.guepardonotes.activities.ActivityDetails;
import guepardoapps.guepardonotes.common.Constants;
import guepardoapps.guepardonotes.model.Note;

import guepardoapps.toolset.services.NavigationService;

public class NoteListAdapter extends BaseAdapter {

	private Context _context;
	private NavigationService _navigationService;

	private ArrayList<Note> _notes;

	private static LayoutInflater _inflater = null;

	public NoteListAdapter(Context context, ArrayList<Note> notes) {
		_context = context;
		_navigationService = new NavigationService(_context);

		_notes = notes;

		_inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return _notes.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class Holder {
		TextView _title;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int index, View convertView, ViewGroup parent) {
		Holder holder = new Holder();
		View rowView = _inflater.inflate(R.layout.list_item, null);

		holder._title = (TextView) rowView.findViewById(R.id.itemTitle);
		holder._title.setText(_notes.get(index).GetTitle());
		holder._title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Bundle details = new Bundle();
				details.putInt(Constants.BUNDLE_NOTE_ID, _notes.get(index).GetId());
				details.putString(Constants.BUNDLE_NOTE_TITLE, _notes.get(index).GetTitle());
				details.putString(Constants.BUNDLE_NOTE_CONTENT, _notes.get(index).GetContent());

				_navigationService.NavigateWithData(ActivityDetails.class, details, true);
			}
		});

		return rowView;
	}
}