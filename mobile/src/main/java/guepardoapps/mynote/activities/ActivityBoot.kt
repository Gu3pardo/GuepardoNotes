package guepardoapps.mynote.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.github.guepardoapps.kulid.ULID
import com.github.guepardoapps.timext.kotlin.extensions.millis
import com.github.guepardoapps.timext.kotlin.postDelayed
import guepardoapps.mynote.R
import guepardoapps.mynote.controller.NavigationController
import guepardoapps.mynote.controller.SharedPreferenceController
import guepardoapps.mynote.controller.SystemInfoController
import guepardoapps.mynote.database.note.DbNote
import guepardoapps.mynote.model.Note

@ExperimentalUnsignedTypes
class ActivityBoot : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_boot)

        val sharedPreferenceController = SharedPreferenceController(this)

        if (!sharedPreferenceController.load(getString(R.string.sharedPrefName), false)) {
            sharedPreferenceController.run {
                save(getString(R.string.sharedPrefBubbleState), true)
                save(getString(R.string.sharedPrefBubblePosX), resources.getInteger(R.integer.sharedPrefBubbleDefaultPosX))
                save(getString(R.string.sharedPrefBubblePosY), resources.getInteger(R.integer.sharedPrefBubbleDefaultPosY))
                save(getString(R.string.sharedPrefName), true)
            }

            DbNote(this).add(Note(id = ULID.random(), title = getString(R.string.title), content = resources.getString(R.string.example)))
        }
    }

    override fun onResume() {
        super.onResume()

        val navigationController = NavigationController(this)
        val systemInfoController = SystemInfoController(this)

        if (systemInfoController.currentAndroidApi() >= android.os.Build.VERSION_CODES.M) {
            if (systemInfoController.checkAPI23SystemPermission(resources.getInteger(R.integer.systemPermissionId))) {
                Handler().postDelayed({ navigationController.navigate(ActivityMain::class.java, true) }, resources.getInteger(R.integer.bootNavigationDelayInMs).millis)
            }
        } else {
            Handler().postDelayed({ navigationController.navigate(ActivityMain::class.java, true) }, resources.getInteger(R.integer.bootNavigationDelayInMs).millis)
        }
    }
}