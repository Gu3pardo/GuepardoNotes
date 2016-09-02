package guepardoapps.guepardonotes;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;

import guepardoapps.common.Constants;
import guepardoapps.toolset.controller.NavigationController;

public class ActivityImpressum extends Activity {

	private Context _context;
	private NavigationController _navigationController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_impressum);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Constants.ACTION_BAR_COLOR));

		_context = this;
		_navigationController = new NavigationController(_context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			_navigationController.NavigateTo(ActivityNotes.class, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}