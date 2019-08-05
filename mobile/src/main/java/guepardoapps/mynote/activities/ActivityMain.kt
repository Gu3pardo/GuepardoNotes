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

    private lateinit var sharedPreferenceController: SharedPreferenceController

    private lateinit var systemInfoController: SystemInfoController

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)

        sharedPreferenceController = SharedPreferenceController(this)
        systemInfoController = SystemInfoController(this)

        listView = findViewById(R.id.listView)
        progressBar = findViewById(R.id.progressBar)

        tryToStopService()

        val navigationController = NavigationController(this)
        findViewById<View>(R.id.btnSettings).setOnClickListener { navigationController.navigate(ActivitySettings::class.java, false) }
        findViewById<View>(R.id.btnAbout).setOnClickListener { navigationController.navigate(ActivityAbout::class.java, false) }
        findViewById<View>(R.id.goToAddView).setOnClickListener { navigationController.navigate(ActivityAdd::class.java, false) }
        findViewById<View>(R.id.btnClose).visibility = View.GONE

        activityCreated = true
    }

    override fun onDestroy() {
        super.onDestroy()
        tryToStartService()
    }

    override fun onPause() {
        super.onPause()
        tryToStartService()
    }

    override fun onResume() {
        super.onResume()

        if (activityCreated) {
            reload()
        }

        tryToStopService()
    }

    private fun reload() {
        listView.adapter = NoteListAdapter(this, DbNote(this).get().toTypedArray()) { reload() }
        progressBar.visibility = View.GONE
        listView.visibility = View.VISIBLE
    }

    private fun tryToStartService() {
        if (!systemInfoController.isServiceRunning(FloatingService::class.java) && sharedPreferenceController.load(getString(R.string.sharedPrefBubbleState), false)) {
            startService(Intent(this, FloatingService::class.java))
        }
    }

    private fun tryToStopService() {
        if (systemInfoController.isServiceRunning(FloatingService::class.java)) {
            stopService(Intent(this, FloatingService::class.java))
        }
    }
}