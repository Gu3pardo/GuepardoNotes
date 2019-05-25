package guepardoapps.mynote.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import guepardoapps.mynote.R
import guepardoapps.mynote.activities.ActivityAbout
import guepardoapps.mynote.activities.ActivityAdd
import guepardoapps.mynote.activities.ActivitySettings
import guepardoapps.mynote.controller.NavigationController
import guepardoapps.mynote.controller.SharedPreferenceController
import guepardoapps.mynote.controller.SystemInfoController
import guepardoapps.mynote.customadapter.NoteListAdapter
import guepardoapps.mynote.database.note.DbNote
import guepardoapps.mynote.utils.Logger

// https://stackoverflow.com/questions/7569937/unable-to-add-window-android-view-viewrootw44da9bc0-permission-denied-for-t#answer-34061521

@ExperimentalUnsignedTypes
class FloatingService : Service() {
    private val tag: String = FloatingService::class.java.simpleName

    private lateinit var navigationController: NavigationController
    private lateinit var systemInfoController: SystemInfoController

    private lateinit var bubbleWindowManager: WindowManager
    private lateinit var listViewWindowManager: WindowManager

    private var bubbleParamsStore: WindowManager.LayoutParams? = null
    private lateinit var bubbleView: ImageView
    private var bubblePosX: Int = 0
    private var bubblePosY: Int = 100
    private var bubbleMoved: Boolean = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        navigationController = NavigationController(this)
        systemInfoController = SystemInfoController(this)

        bubbleWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        listViewWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        bubbleView = ImageView(this)
        initBubbleView()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bubbleWindowManager.removeView(bubbleView)
        } catch (exception: Exception) {
            Logger.instance.error(tag, exception)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("ClickableViewAccessibility")
    private fun initBubbleView() {
        val sharedPreferenceController = SharedPreferenceController(this)

        bubblePosX = sharedPreferenceController.load(getString(R.string.sharedPrefBubblePosX), resources.getInteger(R.integer.sharedPrefBubbleDefaultPosX))
        bubblePosY = sharedPreferenceController.load(getString(R.string.sharedPrefBubblePosY), resources.getInteger(R.integer.sharedPrefBubbleDefaultPosY))

        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.START
        params.x = bubblePosX
        params.y = bubblePosY
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        bubbleParamsStore = params

        val backgroundShape = GradientDrawable()
        backgroundShape.setColor(resources.getColor(R.color.colorPrimaryDark))
        backgroundShape.cornerRadius = 100.0f

        bubbleView.background = backgroundShape
        bubbleView.setImageResource(R.mipmap.ic_launcher)
        bubbleView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (params.x < 0) {
                            params.x = 0
                            bubbleParamsStore = params
                            bubbleWindowManager.updateViewLayout(bubbleView, bubbleParamsStore)
                        }

                        initialX = params.x
                        initialY = params.y

                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        val minMovement = resources.getInteger(R.integer.bubbleMinMove)
                        if (initialX - params.x > minMovement
                                || initialY - params.y > minMovement
                                || params.x - initialX > minMovement
                                || params.y - initialY > minMovement) {
                            bubbleMoved = true
                        }

                        bubbleParamsStore = params
                        bubbleWindowManager.updateViewLayout(bubbleView, bubbleParamsStore)

                        return true
                    }
                }

                return false
            }
        })

        bubbleView.setOnClickListener {
            if (bubbleMoved) {
                bubbleMoved = false

                if (params.x > (systemInfoController.displayDimension().width / 2)) {
                    params.x = systemInfoController.displayDimension().width
                } else {
                    params.x = 0
                }
                bubblePosX = params.x
                bubblePosY = params.y

                sharedPreferenceController.save(getString(R.string.sharedPrefBubblePosX), bubblePosX)
                sharedPreferenceController.save(getString(R.string.sharedPrefBubblePosY), bubblePosY)

                bubbleParamsStore = params
                bubbleWindowManager.updateViewLayout(bubbleView, bubbleParamsStore)
            } else {
                showListView()
            }
        }

        bubbleWindowManager.addView(bubbleView, bubbleParamsStore)
    }

    private fun showListView() {
        val listView = View.inflate(applicationContext, R.layout.side_main, null)

        listView.findViewById<FloatingActionButton>(R.id.btnSettings)
                .setOnClickListener {
                    navigationController.navigate(ActivitySettings::class.java, false)
                    listViewWindowManager.removeView(listView)
                }

        listView.findViewById<FloatingActionButton>(R.id.btnAbout)
                .setOnClickListener {
                    navigationController.navigate(ActivityAbout::class.java, false)
                    listViewWindowManager.removeView(listView)
                }

        listView.findViewById<FloatingActionButton>(R.id.goToAddView)
                .setOnClickListener {
                    navigationController.navigate(ActivityAdd::class.java, false)
                    listViewWindowManager.removeView(listView)
                }

        listView.findViewById<FloatingActionButton>(R.id.btnClose)
                .setOnClickListener {
                    listViewWindowManager.removeView(listView)
                }

        val noteListView = listView.findViewById<ListView>(R.id.listView)
        noteListView.adapter = NoteListAdapter(this, DbNote(this).get().toTypedArray())
        noteListView.visibility = View.VISIBLE
        listView.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

        val layoutParams = WindowManager.LayoutParams().apply {
            gravity = Gravity.CENTER
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            alpha = 1.0f
            packageName = applicationContext.packageName
            buttonBrightness = 1f
            windowAnimations = android.R.style.Animation_Dialog
        }

        listViewWindowManager.addView(listView, layoutParams)
    }
}