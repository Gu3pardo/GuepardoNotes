package guepardoapps.mynote.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import guepardoapps.mynote.R
import guepardoapps.mynote.controller.NavigationController
import guepardoapps.mynote.controller.SharedPreferenceController
import guepardoapps.mynote.controller.SystemInfoController
import guepardoapps.mynote.customadapter.NoteListAdapter
import guepardoapps.mynote.database.note.DbNote
import guepardoapps.mynote.services.FloatingService

@ExperimentalUnsignedTypes
class ActivityMain : Activity() {

    private var activityCreated: Boolean = false

    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar

    private lateinit var navigationController: NavigationController
    private lateinit var sharedPreferenceController: SharedPreferenceController
    private lateinit var systemInfoController: SystemInfoController

    private lateinit var floatingService: Class<FloatingService>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)

        listView = findViewById(R.id.listView)
        progressBar = findViewById(R.id.progressBar)

        navigationController = NavigationController(this)
        sharedPreferenceController = SharedPreferenceController(this)
        systemInfoController = SystemInfoController(this)

        floatingService = FloatingService::class.java
        tryToStopService()

        findViewById<View>(R.id.btnSettings).setOnClickListener { navigationController.navigate(ActivitySettings::class.java, false) }
        findViewById<View>(R.id.btnAbout).setOnClickListener { navigationController.navigate(ActivityAbout::class.java, false) }
        findViewById<View>(R.id.goToAddView).setOnClickListener { navigationController.navigate(ActivityAdd::class.java, false) }
        findViewById<View>(R.id.btnClose).visibility = View.GONE

        activityCreated = true
    }

    override fun onPause() {
        super.onPause()
        tryToStartService()
    }

    override fun onResume() {
        super.onResume()

        if (activityCreated) {
            listView.adapter = NoteListAdapter(this, DbNote(this).get().toTypedArray())
            progressBar.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }

        tryToStopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        tryToStartService()
    }

    private fun tryToStartService() {
        if (!systemInfoController.isServiceRunning(floatingService) && sharedPreferenceController.load(getString(R.string.sharedPrefBubbleState), false)) {
            startService(Intent(this, floatingService))
        }
    }

    private fun tryToStopService() {
        if (systemInfoController.isServiceRunning(floatingService)) {
            stopService(Intent(this, floatingService))
        }
    }
}