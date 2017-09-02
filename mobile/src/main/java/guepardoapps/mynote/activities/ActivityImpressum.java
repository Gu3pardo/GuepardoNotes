package guepardoapps.mynote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import guepardoapps.mynote.R;
import guepardoapps.mynote.common.constants.*;
import guepardoapps.mynote.controller.MailController;
import guepardoapps.mynote.tools.Logger;

public class ActivityImpressum extends Activity {
    private static final String TAG = ActivityImpressum.class.getSimpleName();
    private Logger _logger;

    private Context _context;
    private MailController _mailController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_impressum);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;
        _mailController = new MailController(_context);
    }

    public void SendMail(View view) {
        _logger.Debug("SendMail");
        _mailController.SendMail("guepardoapps@gmail.com", true);
    }

    public void GoToGitHub(View view) {
        _logger.Debug("GoToGitHub");
        String gitHubLink = _context.getString(R.string.gitHubLink);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gitHubLink)));
    }

    public void PayPal(View view) {
        _logger.Debug("PayPal");
        String gitHubLink = _context.getString(R.string.payPalLink);
        Intent gitHubBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gitHubLink));
        _context.startActivity(gitHubBrowserIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        _logger.Debug("onKeyDown");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}