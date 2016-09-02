package guepardoapps.customadapter;

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

import guepardoapps.toolset.classes.Note;
import guepardoapps.common.Constants;
import guepardoapps.toolset.controller.NavigationController;
import guepardoapps.guepardonotes.ActivityDetails;
import guepardoapps.guepardonotes.R;

/**********************************************************
 ********** Custom List adapter for the movies ***********
 **********************************************************/
public class NoteListAdapter extends BaseAdapter {
	/*
	 * variable to prevent "this"
	 */
	private Context _context;

	/*
	 * controller used for navigating through the activities
	 */
	private NavigationController _navigationController;

	/*
	 * variables for the movie displayed in the list and in the details
	 */

	private ArrayList<Note> _notes;

	/*
	 * variable to handle the layout
	 */
	private static LayoutInflater _inflater = null;

	/*
	 * constructor where the entered data is assigned to the arrays
	 */
	public NoteListAdapter(Context context, ArrayList<Note> notes, NavigationController navigationController) {
		_notes = notes;
		_context = context;
		_navigationController = navigationController;
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

	/*
	 * internal class which contains the views a button for the name and loading
	 * the details and a textview for the rating of the movie
	 */
	public class Holder {
		TextView _title;
	}

	/*
	 * creating the view for each entered item
	 */
	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int index, View convertView, ViewGroup parent) {
		/*
		 * creating an instance of the class Holder for each item
		 */
		Holder holder = new Holder();

		/*
		 * inflate the defined view in th elayout for each item
		 */
		View rowView = _inflater.inflate(R.layout.list_item, null);

		/*
		 * setting the text of the name button to the movie title and adding a
		 * onclicklistener to go to the details if clicked
		 */
		holder._title = (TextView) rowView.findViewById(R.id.itemTitle);
		holder._title.setText(_notes.get(index).GetTitle());
		holder._title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*
				 * create a bundle used in a intent which will be send to the
				 * details activity
				 */
				Bundle details = new Bundle();
				details.putInt(Constants.BUNDLE_NOTE_ID, _notes.get(index).GetId());
				details.putString(Constants.BUNDLE_NOTE_TITLE, _notes.get(index).GetTitle());
				details.putString(Constants.BUNDLE_NOTE_CONTENT, _notes.get(index).GetContent());

				/*
				 * navigate using navigationController
				 */
				_navigationController.NavigateWithData(ActivityDetails.class, details, true);
			}
		});

		return rowView;
	}
}