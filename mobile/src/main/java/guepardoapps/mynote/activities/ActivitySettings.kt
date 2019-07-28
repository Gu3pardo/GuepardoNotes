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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_settings)

        val sharedPreferenceController = SharedPreferenceController(this)
        val systemInfoController = SystemInfoController(this)

        switchBubbleState.isChecked = sharedPreferenceController.load(getString(R.string.sharedPrefBubbleState), false)
        switchBubbleState.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferenceController.save(getString(R.string.sharedPrefBubbleState), isChecked)
            if (isChecked) {
                if (systemInfoController.canDrawOverlay()) {
                    systemInfoController.checkAPI23SystemPermission(resources.getInteger(R.integer.systemPermissionId))
                } else {
                    startService(Intent(this, FloatingService::class.java))
                }
            } else {
                if (systemInfoController.isServiceRunning(FloatingService::class.java)) {
                    stopService(Intent(this, FloatingService::class.java))
                }
            }
        }
    }
}