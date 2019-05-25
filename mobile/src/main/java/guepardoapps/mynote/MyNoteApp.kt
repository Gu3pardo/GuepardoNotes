package guepardoapps.mynote

import android.app.Application
import guepardoapps.mynote.utils.Logger

class MyNoteApp : Application() {
    private val tag: String = MyNoteApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        Logger.instance.initialize(this)
        Logger.instance.debug(tag, "onCreate")
    }
}