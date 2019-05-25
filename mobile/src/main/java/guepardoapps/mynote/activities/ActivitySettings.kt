package guepardoapps.mynote.activities

import android.app.Activity
import android.os.Bundle
import guepardoapps.mynote.R
import guepardoapps.mynote.controller.SharedPreferenceController
import guepardoapps.mynote.controller.SystemInfoController
import kotlinx.android.synthetic.main.side_settings.*
import android.content.Intent
import guepardoapps.mynote.services.FloatingService

@ExperimentalUnsignedTypes
class ActivitySettings : Activity() {

    private val _floatingService = FloatingService::class.java

    private lateinit var sharedPreferenceController: SharedPreferenceController
    private lateinit var systemInfoController: SystemInfoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_settings)

        sharedPreferenceController = SharedPreferenceController(this)
        systemInfoController = SystemInfoController(this)

        switchBubbleState.isChecked = sharedPreferenceController.load(getString(R.string.sharedPrefBubbleState), false)
        switchBubbleState.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferenceController.save(getString(R.string.sharedPrefBubbleState), isChecked)
            if (isChecked) {
                if (systemInfoController.canDrawOverlay()) {
                    systemInfoController.checkAPI23SystemPermission(resources.getInteger(R.integer.systemPermissionId))
                } else {
                    startService(Intent(this, _floatingService))
                }
            } else {
                if (systemInfoController.isServiceRunning(_floatingService)) {
                    stopService(Intent(this, _floatingService))
                }
            }
        }
    }
}