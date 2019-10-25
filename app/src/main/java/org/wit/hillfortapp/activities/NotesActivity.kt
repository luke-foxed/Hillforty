package org.wit.hillfortapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_notes.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.Note

class NotesActivity : MainActivity(), AnkoLogger {

    lateinit var app: MainApp
    var note = Note()
    var currentHillfort = HillfortModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setMessage("Save changes to note?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    note.title = noteActivityTitle.text.toString()
                    note.content = noteActivityContent.text.toString()
                    app.users.updateNote(app.activeUser, currentHillfort, note)

                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }

                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            R.id.noteMenuDelete -> {
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setMessage("Do you want to delete this note?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    app.users.deleteNote(app.activeUser, currentHillfort, note)
                    finish()
                }

                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }


        return super.onOptionsItemSelected(item)
    }
}

