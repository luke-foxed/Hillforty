package org.wit.hillfortapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Note

class NotesActivity : MainActivity(), AnkoLogger {

    lateinit var app: MainApp
    private var note = Note()
    private var currentHillfort = HillfortModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_notes, content_frame)
        app = application as MainApp

        if (intent.hasExtra("note_edit")) {
            note = intent.extras?.getParcelable("note_edit")!!
            noteActivityTitle.setText(note.title)
            noteActivityContent.setText(note.content)
        }
        if (intent.hasExtra("current_hillfort")) {
            currentHillfort = intent.extras?.getParcelable("current_hillfort")!!
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.noteMenuBack -> {
                finish()
            }
            R.id.noteMenuEdit -> {
                if (listOf(
                        noteActivityTitle.text.toString(),
                        noteActivityContent.text.toString()
                    ).contains("")
                ) {
                    toast("Please fill out all fields")
                }
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setMessage("Save changes to note?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    note.title = noteActivityTitle.text.toString()
                    note.content = noteActivityContent.text.toString()
                    app.users.updateNote(app.activeUser, currentHillfort, note)
                    dialog.dismiss()

                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            R.id.noteMenuDelete -> {
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setMessage("Do you want to delete this note?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    app.users.deleteNote(app.activeUser, currentHillfort, note)
                    dialog.dismiss()
                    finish()
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }


        return super.onOptionsItemSelected(item)
    }
}

