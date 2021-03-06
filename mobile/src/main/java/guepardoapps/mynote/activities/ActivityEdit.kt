package guepardoapps.mynote.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Scroller
import android.widget.TextView
import android.widget.Toast
import com.rey.material.widget.FloatingActionButton
import es.dmoral.toasty.Toasty
import guepardoapps.mynote.R
import guepardoapps.mynote.controller.DialogController
import guepardoapps.mynote.controller.IDialogController
import guepardoapps.mynote.database.note.DbNote
import guepardoapps.mynote.model.Note

class ActivityEdit : Activity() {

    private lateinit var context: Context

    private val dbNote: DbNote = DbNote(this)

    private val dialogController: IDialogController = DialogController(this)

    private var noteEdited: Boolean = false

    private lateinit var note: Note

    private lateinit var btnDelete: FloatingActionButton

    private lateinit var btnEditSave: FloatingActionButton

    private lateinit var contentView: EditText

    private lateinit var dateTimeView: TextView

    private lateinit var titleView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_details)

        titleView = findViewById(R.id.detailTitle)
        contentView = findViewById(R.id.detailContent)
        dateTimeView = findViewById(R.id.detailDateTime)
        btnEditSave = findViewById(R.id.btnEditSave)
        btnDelete = findViewById(R.id.btnDelete)

        context = this

        val noteId = intent.extras!!.getString(getString(R.string.bundleDataId))
        note = dbNote.get().first { x -> x.id == noteId }

        titleView.setText(note.title)
        dateTimeView.text = note.dateTimeString

        titleView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                noteEdited = true
                btnEditSave.visibility = View.VISIBLE
                btnDelete.visibility = View.INVISIBLE
                note.title = titleView.text.toString()
                dateTimeView.text = note.dateTimeString
            }
        })

        contentView.apply {
            setText(note.content)
            setScroller(Scroller(context))
            isVerticalScrollBarEnabled = true
            movementMethod = ScrollingMovementMethod()
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    noteEdited = true
                    btnEditSave.visibility = View.VISIBLE
                    btnDelete.visibility = View.INVISIBLE
                    note.content = contentView.text.toString()
                    dateTimeView.text = note.dateTimeString
                }
            })
        }

        btnEditSave.setOnClickListener {
            if (dbNote.update(note) != 0) {
                Toasty.error(context, getString(R.string.updateFailedToasty), Toast.LENGTH_LONG).show()
            } else {
                resetEditable()
            }
        }

        btnDelete.setOnClickListener {
            dialogController.createDialog(String.format(getString(R.string.deleteRequest), note.title), getString(R.string.yes), this::deleteNote, getString(R.string.no)) {}
        }

        findViewById<FloatingActionButton>(R.id.btnShare)
                .setOnClickListener {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, note.content)
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (noteEdited) {
                dialogController.createDialog(getString(R.string.unsavedChangesWarning), getString(R.string.yes), this::updateNote, getString(R.string.no), this::finish)
            } else {
                finish()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun deleteNote() {
        if (dbNote.delete(note.id) == 0) {
            Toasty.error(context, getString(R.string.deleteFailedToasty), Toast.LENGTH_LONG).show()
        }
        finish()
    }

    private fun resetEditable() {
        noteEdited = false
        btnEditSave.visibility = View.INVISIBLE
        btnDelete.visibility = View.VISIBLE
        titleView.isFocusable = false
        contentView.isFocusable = false
    }


    private fun updateNote() {
        if (dbNote.update(note) == 0) {
            Toasty.error(context, getString(R.string.updateFailedToasty), Toast.LENGTH_LONG).show()
        } else {
            resetEditable()
        }
    }
}